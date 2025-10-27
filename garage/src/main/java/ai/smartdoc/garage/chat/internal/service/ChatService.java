package ai.smartdoc.garage.chat.internal.service;

import ai.smartdoc.garage.chat.internal.dao.ChatDao;
import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.common.utils.IdCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
class ChatService {

    @Autowired
    ChatDao chatDao;

    Chat createChat(String userId) {
        Chat chat = new Chat(userId);
        return chatDao.save(chat);
    }

    List<Chat> getUserChats(String userId) {
        return chatDao.getUserChats(userId);
    }

    Chat.Document addDocumentInChat(String chatId, MultipartFile file) {
        String docName = file.getOriginalFilename();
        if (docName == null || docName.isEmpty()) {
            throw new GarageException("Filename can't be null or empty", HttpStatus.BAD_REQUEST);
        }

        Optional<Chat> chatOptional = chatDao.getChatById(chatId);
        if (chatOptional.isEmpty()) {
            throw new GarageException("Chat not found", HttpStatus.NOT_FOUND);
        }

        Chat.Document document = Chat.Document.builder()
                .docId(IdCreator.createId(Chat.Document.class))
                .docName(docName)
                .uploadedAt(System.currentTimeMillis())
                .build();

        Chat chat = chatOptional.get();
        if (chat.getDocuments() == null) {
            chat.setDocuments(new ArrayList<>());
        }
        chat.getDocuments().add(document);
        chatDao.save(chat);
        return document;
    }
}
