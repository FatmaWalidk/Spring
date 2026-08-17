package com.fatma.taskmanger.user;

import com.fatma.taskmanger.auth.dto.RegisterRequest;
import com.fatma.taskmanger.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct generates the implementation of this interface at compile time
 * (target/generated-sources). We only declare the *contract*.
 *
 * - id is ignored when creating a new User: the database generates it
 *   (GenerationType.IDENTITY), the client must never choose it.
 * - password/role are ignored on purpose too: hashing the password and
 *   assigning the default role are business rules, not mapping rules,
 *   so they stay in AuthService, not here.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}
