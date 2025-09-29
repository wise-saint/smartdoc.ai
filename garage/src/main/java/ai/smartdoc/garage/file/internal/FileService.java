package ai.smartdoc.garage.file.internal;

import ai.smartdoc.garage.common.dto.Chunk;
import ai.smartdoc.garage.common.dto.UploadResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.embedding.EmbeddingPort;
import ai.smartdoc.garage.file.FilePort;
import ai.smartdoc.garage.qdrant.QdrantPort;
import ai.smartdoc.garage.common.utils.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
class FileService implements FilePort {
    Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    EmbeddingPort embeddingPort;

    @Autowired
    QdrantPort qdrantPort;

    @Override
    public UploadResponse uploadFile(MultipartFile file) throws IOException {
        if (file.getContentType() == null) {
            throw new GarageException("Missing content type", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType().toLowerCase();
        List<String> pageList;

        if(contentType.equals("application/pdf") || contentType.equals("application/x-pdf")) {
            pageList = extractPagesFromPdf(file);
        } else {
            throw new  GarageException("Only PDF supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        List<Pair<String, Integer>> wordPageNoMap = getWordPageNoMap(pageList);
        List<Chunk> chunks = convertToChunks(wordPageNoMap);
        List<List<Float>> embeddingVectors = embeddingPort.getEmbeddingVectors(chunks);
        String docId = qdrantPort.upsertPoints(chunks, embeddingVectors);

        return new UploadResponse(docId, HttpStatus.ACCEPTED);
    }

    private List<String> extractPagesFromPdf(MultipartFile file) {
        List<String> pageList = new ArrayList<>();
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(document);
                if (!text.isEmpty()) pageList.add(text);
            }
        } catch (Exception e) {
            throw new GarageException("Error extracting text from file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return pageList;
    }

    private List<Chunk> convertToChunks(List<Pair<String, Integer>> wordPageNoMap) {
        List<Chunk> chunks = new ArrayList<>();
        int chunkSize = 300; // 300 words per chunk
        int overlap = 30;
        for (int i = 0; i < wordPageNoMap.size(); i += (chunkSize - overlap)) {
            int j = Math.min(i + chunkSize, wordPageNoMap.size());
            StringBuilder chunkText = new StringBuilder();
            for (int k = i; k < j; k++) {
                chunkText.append(wordPageNoMap.get(k).getFirst()).append(" ");
            }
            int startPage = wordPageNoMap.get(i).getSecond();
            int endPage = wordPageNoMap.get(j - 1).getSecond();
            chunks.add(new Chunk(chunkText.toString().trim(), startPage, endPage));
        }
        return chunks;
    }

    private List<Pair<String, Integer>> getWordPageNoMap(List<String> pageList) {
        List<Pair<String, Integer>> wordPageNoMap = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i++) {
            String[] words = pageList.get(i).split("\\s+");
            for (String word : words) {
                wordPageNoMap.add(new Pair<>(word.trim(), i + 1));
            }
        }
        return wordPageNoMap;
    }
}
