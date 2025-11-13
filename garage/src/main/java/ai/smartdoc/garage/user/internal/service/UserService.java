package ai.smartdoc.garage.user.internal.service;

import ai.smartdoc.garage.user.UserPort;
import ai.smartdoc.garage.user.internal.entity.User;
import org.springframework.stereotype.Service;

@Service
class UserService implements UserPort {

    @Override
    public User createUser(String emailId) {
        return null;
    }

    @Override
    public User findUserByEmailId(String emailId) {
        return null;
    }
}
