package io.github.mohamedwasfy.spring.boot.security.repositories;

import io.github.mohamedwasfy.spring.boot.security.entities.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"addresses", "favoriteProducts"})
    List<User> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"addresses", "favoriteProducts"})
    Optional<User> findById(Long id);

    boolean existsByEmail(String email);
}
