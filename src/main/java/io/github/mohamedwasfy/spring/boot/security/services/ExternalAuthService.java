package io.github.mohamedwasfy.spring.boot.security.services;

import io.github.mohamedwasfy.spring.boot.security.config.ExternalAuthConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@Getter
@Setter
@AllArgsConstructor
public class ExternalAuthService {

    private final ExternalAuthConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    private TokenInfo cached;

    public synchronized String getValidToken() {
        if (cached == null || cached.isExpired()) {
            log.info("Fetching new external token");
            cached = requestNewToken();
        }
        return cached.getAccessToken();
    }

    private TokenInfo requestNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(config.getClientId(), config.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "password");

        body.add("username", config.getUsername());

        body.add("password", config.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity (
                    config.getTokenUrl(), request, Map.class);
            return mapToTokenInfo(response.getBody());
        } catch (HttpClientErrorException ex) {
            log.error("Failed to fetch external token: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("External auth failed", ex);
        }
    }

    private TokenInfo mapToTokenInfo(Map body) {
        TokenInfo info = new TokenInfo();
        info.setAccessToken((String) body.get("access_token"));
        int expiresIn = (Integer) body.get("expires_in");
        // 30s safety buffer
        info.setExpiresAt(Instant.now().plusSeconds(expiresIn - 30));
        return info;
    }

    @Getter
    @Setter
    @Service
    private static class TokenInfo {
        private String accessToken;
        private Instant expiresAt;

        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}