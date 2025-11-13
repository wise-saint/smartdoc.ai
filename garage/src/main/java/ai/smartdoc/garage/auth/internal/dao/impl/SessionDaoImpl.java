package ai.smartdoc.garage.auth.internal.dao.impl;

import ai.smartdoc.garage.auth.internal.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SessionDaoImpl implements SessionRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;


}
