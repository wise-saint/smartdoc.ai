package ai.smartdoc.garage.user;

import ai.smartdoc.garage.user.internal.entity.User;

public interface UserPort {

    User createUser(String emailId);

    User findUserByEmailId(String emailId);

}
