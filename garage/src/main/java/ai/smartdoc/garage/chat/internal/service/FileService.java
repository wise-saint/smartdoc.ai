package ai.smartdoc.garage.chat.internal.service;

import ai.smartdoc.garage.chat.internal.dao.FileDao;
import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.common.dto.UploadResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.huggingface.HuggingFacePort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
class FileService {
    Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    HuggingFacePort huggingFacePort;

    @Autowired
    QdrantPort qdrantPort;

    @Autowired
    ChatService chatService;

    @Autowired
    FileDao fileDao;

    UploadResponse uploadFile(String chatId, MultipartFile file) {
        Chat.Document document = chatService.addDocumentInChat(chatId, file);
        String docId = document.getDocId();

        String text = extractText(file);
        List<String> sentenceList = getSentences(text);
        List<Chunk> chunkList = createChunks(sentenceList, chatId, docId);
        preprocessChunks(chunkList);
        fileDao.saveAll(chunkList);

        List<List<Float>> embeddingVectors = huggingFacePort.getEmbeddingVectors(chunkList);
        String upsertStatus = qdrantPort.upsertPoints(chunkList, embeddingVectors, docId, chatId);

        if (!upsertStatus.equalsIgnoreCase("ok")) {
            throw new GarageException("Failed to upsert points in Qdrant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new UploadResponse(docId, HttpStatus.ACCEPTED);
    }

    private String extractText(MultipartFile file) {
        if (file.getContentType() == null) {
            throw new GarageException("Missing content type", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType().toLowerCase();
        String text = null;

        try {
            switch (contentType) {
                case "application/pdf", "application/x-pdf":
                    try (PDDocument document = PDDocument.load(file.getInputStream())) {
                        text = new PDFTextStripper().getText(document);
                    }
                    break;
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword":
                    try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                        StringBuilder sb = new StringBuilder();
                        for (XWPFParagraph para : doc.getParagraphs()) {
                            sb.append(para.getText()).append(" ");
                        }
                        text = sb.toString();
                    }
                    break;
                case "text/plain":
                    text = new String(file.getBytes());
                    break;
                default:
                    throw new GarageException("Unsupported media type: " + contentType, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
        } catch (GarageException g) {
            throw g;
        } catch (Exception e) {
            logger.error("Text extraction failed: {}", e.getMessage(), e);
            throw new GarageException("Text extraction failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return text;
    }

    private List<String> getSentences(String text) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String sentence = text.substring(start, end).trim();
            if (!sentence.isEmpty()) sentences.add(sentence);
        }
        return sentences;
    }

    private List<Chunk> createChunks(List<String> sentenceList, String chatId, String docId) {
        List<Chunk> chunkList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int chunkIndex = 0;
        int chunkSize = 768; // Chars count
        for (int start = 0, itr = 0; itr < sentenceList.size(); itr++) {
            sb.append(sentenceList.get(itr)).append(" ");
            if (sb.length() >= chunkSize || itr == sentenceList.size()-1) {
                Chunk chunk = Chunk.builder()
                        .chatId(chatId)
                        .docId(docId)
                        .chunkIndex(chunkIndex++)
                        .originalText(sb.toString())
                        .build();
                chunkList.add(chunk);
                sb.setLength(0);
                if(itr != sentenceList.size()-1) { // 2 sentence overlap
                    int nextStart  = itr - 1;
                    if(start < nextStart) {
                        start = nextStart;
                        itr = --nextStart;
                    }
                }
            }
        }
        return chunkList;
    }

    private void preprocessChunks(List<Chunk> chunkList) {
        for (Chunk chunk: chunkList) {
            String text = chunk.getOriginalText();
            text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFC);
            text = text.toLowerCase(Locale.ENGLISH);
            text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ");
//            text = text.replaceAll("https?://\\S+\\b", " ");
//            text = text.replaceAll("\\b\\S+@\\S+\\b", " ");
            text = text.replaceAll("\\s+", " ").trim();
            chunk.setPreprocessedText(text);
        }
    }

}
