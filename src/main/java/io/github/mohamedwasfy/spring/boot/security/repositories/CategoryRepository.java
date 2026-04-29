package io.github.mohamedwasfy.spring.boot.security.repositories;

import io.github.mohamedwasfy.spring.boot.security.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}