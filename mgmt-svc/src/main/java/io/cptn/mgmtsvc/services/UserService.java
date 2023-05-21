package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.User;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.repositories.UserRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
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
        return userRepository.findAll(pageable).stream().toList();
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

        if (!fieldErrorList.isEmpty()) {
            throw new BadRequestException("Invalid data", fieldErrorList);
        }
    }
}
