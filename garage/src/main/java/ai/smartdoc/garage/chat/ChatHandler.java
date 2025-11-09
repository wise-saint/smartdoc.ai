package ai.smartdoc.garage.chat;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.chat.internal.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/sd/chat")
@CrossOrigin
public class ChatHandler {

    @Autowired
    ChatPort chatPort;

    @RequestMapping(path = "/createChat", method = RequestMethod.POST)
    public ResponseEntity<Chat> createChat(@RequestParam String userId) {
        return new ResponseEntity<>(chatPort.createChat(userId), HttpStatus.OK);
    }

    @RequestMapping(path = "/getUserChats", method = RequestMethod.GET)
    public ResponseEntity<List<Chat>> getUserChats(String userId) {
        return new ResponseEntity<>(chatPort.getUserChats(userId), HttpStatus.OK);
    }

    @RequestMapping(path = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam String chatId,
                                                     @RequestParam MultipartFile file) {
        return new ResponseEntity<>(chatPort.uploadFile(chatId, file), HttpStatus.OK);
    }

    @RequestMapping(path = "getChatMessages", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getChatMessages(@RequestParam String userId,
                                                         @RequestParam String chatId) {
        return new ResponseEntity<>(chatPort.getChatMessages(userId, chatId), HttpStatus.OK);
    }

    @RequestMapping(path = "/askQuestion", method = RequestMethod.GET)
    public ResponseEntity<String> askQuestion(@RequestParam String chatId, @RequestParam String question) {
        return new ResponseEntity<>(chatPort.askQuestion(chatId, question), HttpStatus.OK);
    }
}
