package ai.smartdoc.garage.chat.internal.dao.impl;

import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

class FileDaoImpl implements FileRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public List<Chunk> getChunksByDocIdAndChunkIndex(List<Chunk> chunkList) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (Chunk chunk: chunkList) {
            criteriaList.add(new Criteria().andOperator(
                    Criteria.where("doc_id").is(chunk.getDocId()),
                    Criteria.where("chunk_index").is(chunk.getChunkIndex())
            ));
        }
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));
        return mongoTemplate.find(query, Chunk.class);
    }
}
