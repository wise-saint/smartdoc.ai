package ai.smartdoc.garage.chat;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.chat.internal.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatPort {

    Chat createChat(String userId);

    List<Chat> getUserChats(String userId);

    UploadResponse uploadFile(String chatId, MultipartFile file);

    List<Message> getChatMessages(String userId, String chatId);

    String askQuestion(String chatId, String question);

}
