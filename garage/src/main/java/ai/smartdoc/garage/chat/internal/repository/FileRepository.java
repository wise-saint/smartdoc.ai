package ai.smartdoc.garage.chat.internal.repository;

import ai.smartdoc.garage.chat.internal.constants.ChunkCollection;
import ai.smartdoc.garage.chat.internal.entity.Chunk;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository {

    List<Chunk> saveAllChunks(List<Chunk> chunksList, ChunkCollection chunkCollection);

    List<Chunk> getChunksByDocIdAndChunkIndex(List<Chunk> chunkList, ChunkCollection chunkCollection);

    List<Chunk> getTopNChunksByBM25Score(String chatId, String question, Integer topN, ChunkCollection chunkCollection);
}
