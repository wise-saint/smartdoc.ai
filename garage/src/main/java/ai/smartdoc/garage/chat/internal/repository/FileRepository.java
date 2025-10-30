package ai.smartdoc.garage.chat.internal.repository;

import ai.smartdoc.garage.chat.internal.entity.Chunk;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository {

    List<Chunk> getChunksByDocIdAndChunkIndex(List<Chunk> chunkList);

    List<Chunk> getTopNChunksByBM25Score(String chatId, String question, Integer topN);
}
