package ai.smartdoc.garage.infra.redis;

public interface RedisPort {

    void set(String key, Object value, Long ttl);

    Object get(String key);

    void delete(String key);
}
