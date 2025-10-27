package ai.smartdoc.garage.chat.internal.dao.impl;

import ai.smartdoc.garage.chat.internal.entity.Chat;
import ai.smartdoc.garage.chat.internal.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

class ChatDaoImpl implements ChatRepository {

    @Autowired
    @Qualifier("sdMongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public List<Chat> getUserChats(String userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("user_id").is(userId)),
                Aggregation.project("chat_id", "chat_title", "user_id", "created_at", "updated_at")
        );
        AggregationResults<Chat> results = mongoTemplate.aggregate(aggregation, "chats", Chat.class);
        return results.getMappedResults();
    }

    @Override
    public Optional<Chat> getChatById(String chatId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chat_id").is(chatId));
        Chat chat = mongoTemplate.findOne(query, Chat.class);
        return Optional.ofNullable(chat);
    }
}
