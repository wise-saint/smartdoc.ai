package ai.smartdoc.garage.chat.internal.service;

import ai.smartdoc.garage.chat.ChatPort;
import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.common.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
class ChatPortAdapter implements ChatPort {

    @Autowired
    ChatService chatService;

    @Autowired
    FileService fileService;

    @Autowired
    MessageService messageService;

    @Override
    public Chat createChat(String userId) {
        return chatService.createChat(userId);
    }

    @Override
    public List<Chat> getUserChats(String userId) {
        return chatService.getUserChats(userId);
    }

    @Override
    public UploadResponse uploadFile(String chatId, MultipartFile file) {
        return fileService.uploadFile(chatId, file);
    }

    @Override
    public List<Message> getChatMessages(String userId, String chatId) {
        return messageService.getChatMessages(userId, chatId);
    }

    @Override
    public String askQuestion(String chatId, String question) {
        return messageService.askQuestion(chatId, question);
    }
}
