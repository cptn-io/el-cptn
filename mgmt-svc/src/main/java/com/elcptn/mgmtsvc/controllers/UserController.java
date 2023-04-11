package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.User;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.mgmtsvc.dto.UserDto;
import com.elcptn.mgmtsvc.mappers.UserMapper;
import com.elcptn.mgmtsvc.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/* @author: kc, created on 4/10/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;
    
    @PostMapping("/api/user")
    public ResponseEntity<UserDto> createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        User user = convert(userDto);
        user = userService.create(user);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri())
                .body(convert(user));
    }

    private User convert(UserDto userDto) {
        return userMapper.toEntity(userDto);
    }

    private UserDto convert(User user) {
        return userMapper.toDto(user);
    }
}
