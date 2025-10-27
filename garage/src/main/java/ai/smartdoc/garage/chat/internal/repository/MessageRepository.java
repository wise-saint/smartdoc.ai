package ai.smartdoc.garage.chat.internal.repository;

import ai.smartdoc.garage.chat.internal.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository {

    List<Message> getChatMessages(String chatId);
}
