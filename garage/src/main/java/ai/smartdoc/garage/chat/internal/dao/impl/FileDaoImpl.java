package ai.smartdoc.garage.chat.internal.dao.impl;

import ai.smartdoc.garage.chat.internal.constants.ChunkCollection;
import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.repository.FileRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

class FileDaoImpl implements FileRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public List<Chunk> saveAllChunks(List<Chunk> chunksList, ChunkCollection chunkCollection) {
        if (chunksList == null || chunksList.isEmpty()) {
            return List.of();
        }
        return (List<Chunk>) mongoTemplate.insert(chunksList, chunkCollection.getName());
    }

    @Override
    public List<Chunk> getChunksByDocIdAndChunkIndex(List<Chunk> chunkList, ChunkCollection chunkCollection) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (Chunk chunk: chunkList) {
            criteriaList.add(new Criteria().andOperator(
                    Criteria.where("doc_id").is(chunk.getDocId()),
                    Criteria.where("chunk_index").is(chunk.getChunkIndex())
            ));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().orOperator(criteriaList.toArray(new Criteria[0]))),
                Aggregation.project("doc_id", "chunk_index", "preprocessed_text")
                        .andExclude("_id")
        );

        AggregationResults<Chunk> results = mongoTemplate.aggregate(aggregation, chunkCollection.getName(), Chunk.class);
        return  results.getMappedResults();
    }

    @Override
    public List<Chunk> getTopNChunksByBM25Score(String chatId, String question, Integer topN, ChunkCollection chunkCollection) {
        List<Document> pipeline = List.of(
                new Document("$search",
                        new Document("index", chunkCollection.getIndexName())
                                .append("compound", new Document()
                                        .append("filter", List.of(
                                                new Document("equals",
                                                        new Document("path", "chat_id")
                                                                .append("value", chatId)
                                                )
                                        ))
                                        .append("must", List.of(
                                                new Document("text",
                                                        new Document("query", question)
                                                                .append("path", "preprocessed_text")
                                                )
                                        ))
                                )
                ),
                new Document("$project", new Document()
                        .append("doc_id", 1)
                        .append("chunk_index", 1)
                        .append("score", new Document("$meta", "searchScore"))
                ),
                new Document("$limit", topN)
        );

        List<Document> docs = mongoTemplate.getCollection(chunkCollection.getName())
                .aggregate(pipeline)
                .into(new java.util.ArrayList<>());

        return docs.stream().map(d -> Chunk.builder()
                .docId(d.getString("doc_id"))
                .chunkIndex(d.getInteger("chunk_index"))
                .score(d.getDouble("score"))
                .build()
        ).toList();
    }
}
