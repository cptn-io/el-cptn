package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.User;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.repositories.UserRepository;
import com.elcptn.common.services.CommonService;
import com.elcptn.common.web.ListEntitiesParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 4/10/23 */
@Service
@RequiredArgsConstructor
public class UserService extends CommonService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    @Cacheable(value = "users", key = "#id")
    public User getUserById(UUID id) {
        Optional<User> userOptional = getById(id);
        if (userOptional.isEmpty()) {
            return null;
        }
        return userOptional.get();
    }


    public User create(User user) {
        user.setDisabled(false);
        user.setLockedOut(false);

        validate(user);
        return userRepository.save(user);
    }

    public List<User> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return userRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    @CacheEvict(value = "users", key = "#user.id")
    public User update(User user) {
        validate(user);
        return userRepository.save(user);
    }

    public long count() {
        return userRepository.count();
    }

    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
    }

    public void validate(User user) {
        List<FieldError> fieldErrorList = new ArrayList<>();

        String email = user.getEmail();

        if (email != null) {
            User existingUser = userRepository.findFirstByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                fieldErrorList.add(new FieldError("user", "email", "User account already exists with the provided " +
                        "email address"));
            }
        }

        if (fieldErrorList.size() > 0) {
            throw new BadRequestException("Invalid data", fieldErrorList);
        }
    }
}
