package ai.smartdoc.garage.common.dto;

import lombok.Data;

@Data
public class JwtResponse {

    private String userId;
    private String emailId;
    private String accessToke;
    private String refreshToken;
    private Long accessExpiresAt;
    private Long refreshExpiresAt;

}
