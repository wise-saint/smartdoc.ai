package ai.smartdoc.garage.chat.internal.dao;

import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.chat.internal.repository.MessageRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageDao extends MessageRepository, MongoRepository<Message, String> {
}
