package ai.smartdoc.garage.auth.internal.dao;

import ai.smartdoc.garage.auth.internal.entity.Session;
import ai.smartdoc.garage.auth.internal.repository.SessionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionDao extends SessionRepository, MongoRepository<Session, String> {
}
