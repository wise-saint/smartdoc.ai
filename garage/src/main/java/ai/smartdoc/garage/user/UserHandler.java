package ai.smartdoc.garage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/sd/user")
@CrossOrigin
public class UserHandler {

    @Autowired
    UserPort userPort;
}
