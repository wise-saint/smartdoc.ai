package ai.smartdoc.garage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sd/user")
@CrossOrigin
public class UserHandler {

    @Autowired
    UserPort userPort;


}
