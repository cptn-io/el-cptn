package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.User;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.UserDto;
import io.cptn.mgmtsvc.mappers.UserMapper;
import io.cptn.mgmtsvc.security.UserPrincipal;
import io.cptn.mgmtsvc.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping("/api/user/{id}")
    public ResponseEntity<UserDto> get(@PathVariable UUID id) {
        User user = getById(id);
        return ResponseEntity.ok(convert((user)));
    }


    @GetMapping("/api/user")
    public ResponseEntity<List<UserDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);


        List<UserDto> userList = userService.getAll(listParam).stream()
                .map(this::convert).toList();
        long count = userService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(userList);

    }


    @PutMapping("/api/user/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id,
                                          @Validated(OnUpdate.class) @RequestBody UserDto userDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        User user = getById(id);
        userMapper.partialUpdate(userDto, user);

        return ResponseEntity.ok(convert(userService.update(user)));
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<UserDto> disableUser(@PathVariable UUID id) {
        User user = getById(id);
        user.setDisabled(true);
        return ResponseEntity.ok(convert(userService.update(user)));
    }


    @GetMapping("/mgmt/myprofile")
    public ResponseEntity<Void> myProfile() {
        UserPrincipal
                principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.status(302).header("Location", "/app/users/" + principal.getId()).build();
    }

    private User getById(UUID id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found with the passed id");
        }

        return userOptional.get();
    }

    private User convert(UserDto userDto) {
        return userMapper.toEntity(userDto);
    }

    private UserDto convert(User user) {
        return userMapper.toDto(user);
    }
}
