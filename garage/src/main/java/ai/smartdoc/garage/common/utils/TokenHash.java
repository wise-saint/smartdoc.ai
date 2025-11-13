package ai.smartdoc.garage.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class TokenHash {

    public static String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }
}
