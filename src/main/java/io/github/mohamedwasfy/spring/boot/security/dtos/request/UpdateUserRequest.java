package io.github.mohamedwasfy.spring.boot.security.dtos.request;
import lombok.Data;

@Data
public class UpdateUserRequest {
    public String name;
    public String email;
}