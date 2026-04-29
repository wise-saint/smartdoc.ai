package ai.smartdoc.garage.user;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.user.internal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sd/user")
@CrossOrigin
public class UserHandler {

    @Autowired
    UserPort userPort;

    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<User> editUser(@RequestBody User user) {
        return new ResponseEntity<>(userPort.editUser(user), HttpStatus.OK);
    }

}
