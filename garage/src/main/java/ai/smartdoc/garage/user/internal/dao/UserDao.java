package ai.smartdoc.garage.user.internal.dao;

import ai.smartdoc.garage.user.internal.entity.User;
import ai.smartdoc.garage.user.internal.repository.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDao extends UserRepository, MongoRepository<User, String> {
}
