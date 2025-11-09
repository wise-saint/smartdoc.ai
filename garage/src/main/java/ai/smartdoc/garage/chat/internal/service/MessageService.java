package ai.smartdoc.garage.chat.internal.service;

import ai.smartdoc.garage.chat.internal.dao.ChatDao;
import ai.smartdoc.garage.chat.internal.dao.FileDao;
import ai.smartdoc.garage.chat.internal.dao.MessageDao;
import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.infra.cohere.CoherePort;
import ai.smartdoc.garage.common.dto.CohereRerankResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.common.utils.IdCreator;
import ai.smartdoc.garage.infra.huggingface.HuggingFacePort;
import ai.smartdoc.garage.infra.qdrant.QdrantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class MessageService {
    Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    HuggingFacePort huggingFacePort;

    @Autowired
    QdrantPort qdrantPort;

    @Autowired
    CoherePort coherePort;

    @Autowired
    MessageDao messageDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    ChatDao chatDao;

    private static final Integer topN = 100;
    private static final Integer defaultK = 60;

    private Message saveMessage(String chatId, String messageText, String sender) {
        Message message = Message.builder()
                .chatId(chatId)
                .messageId(IdCreator.createId(Message.class))
                .sender(sender)
                .message(messageText)
                .build();
        return messageDao.save(message);
    }

    List<Message> getChatMessages(String userId, String chatId) {
        return messageDao.getChatMessages(chatId);
    };

    String askQuestion(String chatId, String question) {
        if (question == null || question.trim().split("\\s+").length < 5) {
            throw new GarageException("Please provide more context", HttpStatus.BAD_REQUEST);
        }
        Optional<Chat> chatOptional = chatDao.getChatById(chatId);
        if (chatOptional.isEmpty()) {
            throw new GarageException("Chat not found", HttpStatus.NOT_FOUND);
        }
        if (chatOptional.get().getDocuments() == null || chatOptional.get().getDocuments().isEmpty()) {
            throw new GarageException("No document uploaded to query", HttpStatus.PRECONDITION_FAILED);
        }
        question = preprocessQuestion(question);

        List<Float> embeddingVector = huggingFacePort.getEmbeddingVectors(question);
        List<Chunk> topQdrantChunks = qdrantPort.queryPoints(embeddingVector, chatId, topN);
        List<Chunk> topBm25Chunks = fileDao.getTopNChunksByBM25Score(chatId, question, topN);
        List<Chunk> fusedChunkList = applyReciprocalRankFusion(topQdrantChunks, topBm25Chunks);
        fusedChunkList = fileDao.getChunksByDocIdAndChunkIndex(fusedChunkList.subList(0, Math.min(fusedChunkList.size(), 30)));

        List<String> documents = new ArrayList<>();
        for (Chunk chunk: fusedChunkList) {
            if (chunk != null && chunk.getPreprocessedText() != null && !chunk.getPreprocessedText().isEmpty()) {
                documents.add(chunk.getPreprocessedText());
            }
        }
        List<CohereRerankResponse> cohereRerankResponseList = coherePort.cohereRerank(question, documents, 5);

        StringBuilder contextBuilder = new StringBuilder();
        int chunkNumber = 1;
        for (CohereRerankResponse response : cohereRerankResponseList) {
            if (response.getScore() > 0.3 && response.getDocument() != null && !response.getDocument().isEmpty()) {
                contextBuilder.append("Chunk ").append(chunkNumber++).append(": ")
                        .append(response.getDocument()).append("\n");
                if (chunkNumber == 4) break;
            }
        }
        String context = contextBuilder.toString();

        String answer = null;

        if (!context.isEmpty()) {
            List<Message> chatHistory = messageDao.getChatMessages(chatId);
            answer = huggingFacePort.completeChat(context, question, chatHistory);
        }
        if (answer == null || answer.isEmpty()) {
            throw new GarageException("Failed to generate answer, try rephrasing your query", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        saveMessage(chatId, question, "user");
        saveMessage(chatId, answer, "assistant");
        return answer;
    }

    private String preprocessQuestion(String question) {
        question = java.text.Normalizer.normalize(question, java.text.Normalizer.Form.NFC);
        question = question.toLowerCase(Locale.ENGLISH);
        question = question.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");
        return question.replaceAll("\\s+", " ").trim();
    }

    private List<Chunk> applyReciprocalRankFusion(List<Chunk> topQdrantChunks, List<Chunk> topBm25Chunks) {
        HashMap<String, Double> hashMap = new HashMap<>();
        for (int i = 0; i < topQdrantChunks.size(); i++) {
            Chunk chunk = topQdrantChunks.get(i);
            String id = chunk.getDocId() + "#" + chunk.getChunkIndex().toString();
            hashMap.put(id, 1.0/(defaultK + i + 1));
        }

        for (int i = 0; i < topBm25Chunks.size(); i++) {
            Chunk chunk = topBm25Chunks.get(i);
            String id = chunk.getDocId() + "#" + chunk.getChunkIndex().toString();
            if (hashMap.get(id) == null) {
                hashMap.put(id, 1.0/(defaultK + i + 1));
            } else {
                hashMap.put(id, hashMap.get(id) + 1.0/(defaultK + i + 1));
            }
        }

        List<Map.Entry<String, Double>> list = new ArrayList<>(hashMap.entrySet());
        list.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        List<Chunk> chunkList = new ArrayList<>();
        for (Map.Entry<String, Double> entry: list) {
            String [] arr = entry.getKey().split("#");
            chunkList.add(Chunk.builder()
                    .docId(arr[0])
                    .chunkIndex(Integer.parseInt(arr[1]))
                    .score(entry.getValue())
                    .build());
        }

        return chunkList;
    }
}
