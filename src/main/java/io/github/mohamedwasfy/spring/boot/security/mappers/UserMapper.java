package io.github.mohamedwasfy.spring.boot.security.mappers;

import io.github.mohamedwasfy.spring.boot.security.dtos.request.RegisterUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.UpdateUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.UserDto;
import io.github.mohamedwasfy.spring.boot.security.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
//  Target        Source
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}

// Libraries
// ModelMapper.
// MapStruct.
// EXTRACTING QUERY PARAMETERS.
// filtering.
// Sorting.
// Pagination.
// sort & page.