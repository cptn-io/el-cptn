package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.User;
import io.cptn.mgmtsvc.dto.UserDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "hashedPassword", expression = "java(passwordToHashedPassword(userDto.getPassword(), null))")
    public abstract User toEntity(UserDto userDto);

    public abstract UserDto toDto(User user);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "hashedPassword", expression = "java(passwordToHashedPassword(userDto.getPassword(), user.getHashedPassword()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User partialUpdate(UserDto userDto, @MappingTarget User user);

    protected String passwordToHashedPassword(String password, String currentPassword) {
        if (password == null) {
            return currentPassword;
        }
        return passwordEncoder.encode(password);
    }
}