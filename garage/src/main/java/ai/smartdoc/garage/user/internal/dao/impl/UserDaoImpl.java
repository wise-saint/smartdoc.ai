package ai.smartdoc.garage.user.internal.dao.impl;

import ai.smartdoc.garage.user.internal.entity.User;
import ai.smartdoc.garage.user.internal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

public class UserDaoImpl implements UserRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public Optional<User> findUserByEmailId(String emailId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email_id").is(emailId));
        User user = mongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(user);
    }
}
