package ai.smartdoc.garage.auth;

import ai.smartdoc.garage.common.dto.AuthApiResponse;
import ai.smartdoc.garage.common.dto.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sd/auth")
@CrossOrigin
public class AuthHandler {

    @Autowired
    AuthPort authPort;

    @RequestMapping(path = "requestOtp", method = RequestMethod.POST)
    public ResponseEntity<AuthApiResponse> requestOtp(@RequestParam String emailId) {
        return new ResponseEntity<>(authPort.requestOtp(emailId), HttpStatus.OK);
    }

    @RequestMapping(path = "verifyOtp", method = RequestMethod.POST)
    public ResponseEntity<JwtResponse> verifyOtp(@RequestParam String emailId,
                                                  @RequestParam String otp,
                                                  HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(authPort.verifyOtp(emailId, otp, httpServletRequest), HttpStatus.OK);
    }

    @RequestMapping(path = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<JwtResponse> refreshToken(@RequestParam String refreshToken,
                                                        HttpServletRequest request) {
        return new ResponseEntity<>(authPort.refreshToken(refreshToken, request), HttpStatus.OK);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<AuthApiResponse> logout(@RequestParam String refreshToken) {
        return new ResponseEntity<>(authPort.logout(refreshToken), HttpStatus.OK);
    }

}
