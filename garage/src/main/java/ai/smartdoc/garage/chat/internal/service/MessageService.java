package ai.smartdoc.garage.chat.internal.service;

import ai.smartdoc.garage.chat.internal.dao.FileDao;
import ai.smartdoc.garage.chat.internal.dao.MessageDao;
import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.cohere.CoherePort;
import ai.smartdoc.garage.common.dto.CohereRerankResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.huggingface.HuggingFacePort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    List<Message> getChatMessages(String userId, String chatId) {
        return messageDao.getChatMessages(chatId);
    };

    String askQuestion(String chatId, String question) {
        question = preprocessQuestion(question);
        List<Float> embeddingVector = huggingFacePort.getEmbeddingVectors(question);
        List<Chunk> chunkList = qdrantPort.queryPoints(embeddingVector, chatId, 20);
        chunkList = fileDao.getChunksByDocIdAndChunkIndex(chunkList);

        List<String> documents = new ArrayList<>();
        for (Chunk chunk: chunkList) {
            if (chunk != null && chunk.getPreprocessedText() != null && !chunk.getPreprocessedText().isEmpty()) {
                documents.add(chunk.getPreprocessedText());
            }
        }
        List<CohereRerankResponse> cohereRerankResponseList = coherePort.cohereRerank(question, documents, 5);

        StringBuilder contextBuilder = new StringBuilder();
        int chunkNumber = 1;
        for (CohereRerankResponse response : cohereRerankResponseList) {
            if (response.getScore() > 0.5 && response.getDocument() != null && !response.getDocument().isEmpty()) {
                contextBuilder.append("Chunk ").append(chunkNumber++).append(": ")
                        .append(response.getDocument()).append("\n");
                if (chunkNumber == 4) break;
            }
        }
        String context = contextBuilder.toString();

        String answer = null;
        if (!context.isEmpty()) {
            answer = huggingFacePort.completeChat(context, question);
        }
        if (answer == null || answer.isEmpty()) {
            throw new GarageException("Failed to generate answer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return answer;
    }

    private String preprocessQuestion(String question) {
        question = java.text.Normalizer.normalize(question, java.text.Normalizer.Form.NFC);
        question = question.toLowerCase(Locale.ENGLISH);
        question = question.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");
        return question.replaceAll("\\s+", " ").trim();
    }
}
