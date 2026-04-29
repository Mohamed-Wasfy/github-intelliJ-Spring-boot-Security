package io.github.mohamedwasfy.spring.boot.security.repositories;

import io.github.mohamedwasfy.spring.boot.security.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}