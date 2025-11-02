package ai.smartdoc.garage.chat.internal.config;

import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfig {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void applyIndexing() {

        // Chat
        mongoTemplate.indexOps("chats").createIndex(new Index().on("chat_id", Sort.Direction.ASC));
        mongoTemplate.indexOps("chats").createIndex(new Index().on("user_id", Sort.Direction.ASC));

        // Chunk
        mongoTemplate.indexOps("chunks_128").createIndex(
                new CompoundIndexDefinition(new Document("doc_id", 1)
                        .append("chunk_index", 1))
                        .named("doc_chunk_idx")
        );
        mongoTemplate.indexOps("chunks_768").createIndex(
                new CompoundIndexDefinition(new Document("doc_id", 1)
                        .append("chunk_index", 1))
                        .named("doc_chunk_idx")
        );

        // Messages
        mongoTemplate.indexOps("messages").createIndex(new Index().on("chat_id", Sort.Direction.ASC));

    }
}
