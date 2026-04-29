package ai.smartdoc.garage.user.internal.repository;

import ai.smartdoc.garage.user.internal.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<User> findUserByEmailId(String emailId);
}
