package ai.smartdoc.garage.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sd/qna")
@CrossOrigin
public class QnaHandler {

    @Autowired
    QnAPort qnAPort;

    @RequestMapping(path = "/ask", method = RequestMethod.GET)
    public ResponseEntity<String> askQuestion(@RequestParam String docId, @RequestParam String question) {
        return new ResponseEntity<>(qnAPort.askQuestion(docId, question), HttpStatus.OK);
    }

}