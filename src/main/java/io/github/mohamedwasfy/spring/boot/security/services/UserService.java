package io.github.mohamedwasfy.spring.boot.security.services;

import io.github.mohamedwasfy.spring.boot.security.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@org.jspecify.annotations.NonNull String email) throws UsernameNotFoundException {
         var user = userRepository.findByEmail(email).orElseThrow (
                () -> new UsernameNotFoundException("User not found")
         );

    return new User (
        user.getEmail(),
        user.getPassword(),
        Collections.emptyList()
    );
    }
}