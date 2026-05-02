package io.github.mohamedwasfy.spring.boot.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    public String token;
}