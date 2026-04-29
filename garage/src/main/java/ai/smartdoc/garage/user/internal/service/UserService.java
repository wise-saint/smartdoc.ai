package ai.smartdoc.garage.user.internal.service;

import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.common.utils.IdCreator;
import ai.smartdoc.garage.user.UserPort;
import ai.smartdoc.garage.user.internal.dao.UserDao;
import ai.smartdoc.garage.user.internal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserService implements UserPort {

    @Autowired
    UserDao userDao;

    @Override
    public User createUser(String emailId) {
        User user = new User();
        user.setUserId(IdCreator.createId(User.class));
        user.setEmailId(emailId);
        user.setIsVerified(true);
        user.setIsActive(true);
        return userDao.save(user);
    }

    @Override
    public User findUserByEmailId(String emailId) {
        Optional<User> userOptional = userDao.findUserByEmailId(emailId);
        if (userOptional.isEmpty()) {
            throw new GarageException("User not found", HttpStatus.NOT_FOUND);
        }
        return userOptional.get();
    }

    @Override
    public User editUser(User user) {
        return userDao.save(user);
    }

}
