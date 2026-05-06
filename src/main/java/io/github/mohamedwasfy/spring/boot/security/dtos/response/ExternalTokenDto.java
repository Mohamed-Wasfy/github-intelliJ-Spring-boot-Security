package io.github.mohamedwasfy.spring.boot.security.dtos.response;
import lombok.Data;

@Data
public class ExternalTokenDto {
    private String access_token;
    private String id_token;
    private String refresh_token;
    private String token_type;
    private Long expires_in;
    private String scope;
    private Long refresh_expires_in;
    private boolean refresh_revocable;
    private boolean revocable;
    private String jti;
}