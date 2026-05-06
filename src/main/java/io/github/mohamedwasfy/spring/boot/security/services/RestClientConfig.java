package io.github.mohamedwasfy.spring.boot.security.services;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@Builder
@RequiredArgsConstructor
public class RestClientConfig {
//
//    private final String authorization;
//
//    @Bean
//    public RestClient apiClient() {
//        return
//            RestClient.builder()
//                .baseUrl("https://tenant1.sasserver.demo.sas.com")
//                .defaultHeader("Accept", "application/json")
//                .defaultHeader("Authorization", "Bearer " + authorization)
//                .defaultHeader("Content-Type", "application/json")
//                .requestInterceptor((req, body, execution) -> {
//                    System.out.println(">> " + req.getMethod() + " " + req.getURI());
//                    return execution.execute(req, body);
//            })
//                .build();
//    }
}