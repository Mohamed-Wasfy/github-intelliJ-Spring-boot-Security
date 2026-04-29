package io.github.mohamedwasfy.spring.boot.security.repositories;

import io.github.mohamedwasfy.spring.boot.security.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}