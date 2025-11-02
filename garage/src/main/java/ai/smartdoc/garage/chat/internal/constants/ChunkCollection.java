package ai.smartdoc.garage.chat.internal.constants;

import lombok.Getter;

@Getter
public enum ChunkCollection {

    CHUNKS_128("chunks_128", 96, "bm25_chunk_128_index"), // 128 tokens
    CHUNKS_768("chunks_768", 576, "bm25_chunk_768_index"); // 768 tokens

    private final String name;
    private final int size;
    private final String indexName;

    ChunkCollection(String name, int size, String indexName) {
        this.name = name;
        this.size = size;
        this.indexName = indexName;
    }

}
