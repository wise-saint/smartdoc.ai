package ai.smartdoc.garage.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/sd/qna")
@CrossOrigin
public class QnAHandler {

    @Autowired
    QnAPort qnAPort;

}
