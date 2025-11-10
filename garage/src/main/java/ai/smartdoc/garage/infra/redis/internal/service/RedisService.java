package ai.smartdoc.garage.infra.redis.internal.service;

import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.infra.redis.RedisPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
class RedisService implements RedisPort {
    Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    @Qualifier("sdRedisTemplate")
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, Long ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            logger.debug("Redis entry set: key = '{}', ttl = {}s", key, ttl);
        } catch (Exception e) {
            logger.error("Error setting key '{}' in Redis", key, e);
            throw new GarageException("Failed to set key-value in Redis: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Error fetching value for key '{}' from Redis", key, e);
            throw new GarageException("Failed to fetch value from Redis: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (Boolean.FALSE.equals(deleted)) {
                logger.warn("No Redis entry found for key: {}", key);
            }
        } catch (Exception e) {
            logger.error("Error deleting key '{}' from Redis ", key, e);
            throw new GarageException("Failed to delete key from Redis: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
