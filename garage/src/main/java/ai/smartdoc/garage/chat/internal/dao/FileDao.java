package ai.smartdoc.garage.chat.internal.dao;

import ai.smartdoc.garage.chat.internal.entity.Chunk;
import ai.smartdoc.garage.chat.internal.repository.FileRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileDao extends FileRepository, MongoRepository<Chunk, String> {
}
