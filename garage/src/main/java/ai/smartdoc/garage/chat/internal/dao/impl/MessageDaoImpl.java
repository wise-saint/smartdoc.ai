package ai.smartdoc.garage.chat.internal.dao.impl;

import ai.smartdoc.garage.chat.internal.entity.Message;
import ai.smartdoc.garage.chat.internal.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

class MessageDaoImpl implements MessageRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public List<Message> getChatMessages(String chatId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chat_id").is(chatId));
        query.with(Sort.by(Sort.Direction.ASC, "sent_at"));
        return mongoTemplate.find(query, Message.class);
    }
}
