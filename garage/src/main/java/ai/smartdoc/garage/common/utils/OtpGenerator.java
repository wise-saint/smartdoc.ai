package ai.smartdoc.garage.common.utils;

import java.security.SecureRandom;

public class OtpGenerator {

    public static String generateOtp(int len) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
