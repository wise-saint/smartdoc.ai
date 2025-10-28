package ai.smartdoc.garage.common.utils;

import java.security.SecureRandom;
import java.time.Instant;

public class IdCreator {

    public static <T> String createId(Class<T> tClass) {
        SecureRandom random = new SecureRandom();

        String name = tClass.getSimpleName().toUpperCase();
        String prefix = name.length() >= 2 ? name.substring(0, 2) : name;

        // 5 random alphanumeric characters (0-9, a-z, A-Z)
        String alphaNum = random.ints(5, 0, 62)  // 0-9 + a-z + A-Z
                .mapToObj(i -> {
                    if (i < 10) return String.valueOf(i);            // 0-9
                    else if (i < 36) return String.valueOf((char) ('a' + i - 10)); // a-z
                    else return String.valueOf((char) ('A' + i - 36));           // A-Z
                })
                .reduce("", String::concat);

        // Timestamp component: last 5 digits of epoch milliseconds
        String timestamp = String.format("%05d", Instant.now().toEpochMilli() % 100_000);

        return prefix + alphaNum + "-" + timestamp;
    }
}
