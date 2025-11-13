package ai.smartdoc.garage.auth.internal.service;

import ai.smartdoc.garage.auth.AuthPort;
import ai.smartdoc.garage.auth.internal.entity.Session;
import ai.smartdoc.garage.common.dto.AuthApiResponse;
import ai.smartdoc.garage.common.dto.JwtResponse;
import ai.smartdoc.garage.common.exception.GarageException;
import ai.smartdoc.garage.common.utils.OtpGenerator;
import ai.smartdoc.garage.common.utils.TokenHash;
import ai.smartdoc.garage.infra.email.EmailPort;
import ai.smartdoc.garage.infra.redis.RedisPort;
import ai.smartdoc.garage.user.UserPort;
import ai.smartdoc.garage.user.internal.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.time.Duration;

class AuthService implements AuthPort {

    @Autowired
    RedisPort redisPort;

    @Autowired
    EmailPort emailPort;

    @Autowired
    UserPort userPort;

    @Autowired
    SessionService sessionService;

    @Override
    public AuthApiResponse requestOtp(String emailId) {
        String otp = OtpGenerator.generateOtp(6);

        String otpHash = TokenHash.hash(otp);
        String key = "OTP: " + emailId;
        redisPort.set(key, otpHash, 300L);

        String subject = "smartdoc.ai signup OTP";
        String body = "Please use this OTP to verify your email. \nOTP: " + otp;
        emailPort.sendEmail(emailId, subject, body);

        return new AuthApiResponse("OTP sent successfully", HttpStatus.OK);
    }

    @Override
    public JwtResponse verifyOtp(String emailId, String otp, HttpServletRequest httpServletRequest) {
        String key = "OTP: " + emailId;
        Object storedHashObj = redisPort.get(key);
        if (storedHashObj == null) {
            throw new GarageException("OTP expired", HttpStatus.NOT_ACCEPTABLE);
        }

        String storedHash = (String) storedHashObj;
        String inputHash = TokenHash.hash(otp);
        if (inputHash.equals(storedHash)) {
            throw new GarageException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        redisPort.delete(key);

        User user = userPort.findUserByEmailId(emailId);
        if (user == null) {
            user = userPort.createUser(emailId);
        }

        Session session = sessionService.createSession(user.getUserId(), httpServletRequest);
        return null;
    }

    @Override
    public JwtResponse refreshToken(String refreshToken, HttpServletRequest request) {
        return null;
    }

    @Override
    public AuthApiResponse logout(String refreshToken) {
        return null;
    }
}
