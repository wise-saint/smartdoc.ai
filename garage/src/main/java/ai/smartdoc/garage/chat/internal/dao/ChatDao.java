package ai.smartdoc.garage.chat.internal.dao;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.repository.ChatRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatDao extends ChatRepository, MongoRepository<Chat, String> {
}
