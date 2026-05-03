package io.github.mohamedwasfy.spring.boot.security.controllers;

import io.github.mohamedwasfy.spring.boot.security.dtos.request.ChangePasswordRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.RegisterUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.UpdateUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.UserDto;
import io.github.mohamedwasfy.spring.boot.security.mappers.UserMapper;
import io.github.mohamedwasfy.spring.boot.security.repositories.UserRepository;
import io.github.mohamedwasfy.spring.boot.security.services.ExternalAuthService;
import io.github.mohamedwasfy.spring.boot.security.services.JwtService;
import io.github.mohamedwasfy.spring.boot.security.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getAllUsers (
        @RequestHeader String authorization,
        @RequestParam(required = false, defaultValue = "", name = "sortBy") String sortBy
    ) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or malformed Authorization header");
        }

        String token = authorization.substring(7);

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }


        if(!Set.of("name", "email").contains(sortBy)) {
            sortBy = "name";
        }

        return ResponseEntity.ok (
            userRepository.findAll(Sort.by(sortBy).ascending())
            .stream()
            .map(userMapper::toDto)
            .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

//        var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    // MethodArgumentNotValidException
    public ResponseEntity<?> registerUser (
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        if(userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already registered.")
            );
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser (
        @PathVariable(name = "id") Long id,
        @RequestBody UpdateUserRequest request
    ) {

        var existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, existingUser);
        userRepository.save(existingUser);

        return ResponseEntity.ok(userMapper.toDto(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        var userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(userToDelete);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword (
        @PathVariable Long id,
        @RequestBody ChangePasswordRequest request
    ) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}