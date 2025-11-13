package ai.smartdoc.garage.auth;

import ai.smartdoc.garage.common.dto.AuthApiResponse;
import ai.smartdoc.garage.common.dto.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthPort {

    AuthApiResponse requestOtp(String emailId);

    JwtResponse verifyOtp(String emailId, String otp, HttpServletRequest httpServletRequest);

    JwtResponse refreshToken(String refreshToken, HttpServletRequest request);

    AuthApiResponse logout(String refreshToken);
}
