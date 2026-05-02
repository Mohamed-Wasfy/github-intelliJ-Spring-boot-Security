package io.github.mohamedwasfy.spring.boot.security.controllers;

import io.github.mohamedwasfy.spring.boot.security.config.JwtConfig;
import io.github.mohamedwasfy.spring.boot.security.dtos.JwtResponse;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.LoginRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.UserDto;
import io.github.mohamedwasfy.spring.boot.security.mappers.UserMapper;
import io.github.mohamedwasfy.spring.boot.security.repositories.UserRepository;
import io.github.mohamedwasfy.spring.boot.security.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login (
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        authenticationManager.authenticate (
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var accessToken = jwtService.generateAccessToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);

        cookie.setPath("/auth/refresh");

        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7d.

        cookie.setSecure(false);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    };

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {
        System.out.println("Validate called");

        var token = authHeader.replace("Bearer ", "");
//        var token = authHeader.substring(7); // "Bearer "

        return jwtService.validateToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
//      Extracting Current Principal.
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

//      Getting User from DB.
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

//      Mapping User to UserDto
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}


//    @PostMapping("/login")
//    public ResponseEntity<Void> login (
//        @Valid @RequestBody LoginRequest request
//    ) {
//        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        return ResponseEntity.ok().build();
//    }

// 403 FORBIDDEN
// You're authenticated but don't have permission to access to this resource.
// 401 UNAUTHORIZED
// You're not authenticated or your credentials are invalid
// MANAGING SECRETS

// UNDERSTANDING FILTERS.
// FILTER
// A class that runs before controllers and can inspect or modify HTTP requests.

// FILTERS
// Check for authentication.
// Log incoming requests.
// Modify headers.
// Block suspicious traffic.