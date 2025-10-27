package ai.smartdoc.garage.chat.internal.repository;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository {

    List<Chat> getUserChats(String userId);

    Optional<Chat> getChatById(String chatId);
}
