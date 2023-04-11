package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.User;
import com.elcptn.common.repositories.UserRepository;
import com.elcptn.common.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* @author: kc, created on 4/10/23 */
@Service
@RequiredArgsConstructor
public class UserService extends CommonService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public User create(User user) {
        user.setDisabled(false);
        user.setLockedOut(false);
        return userRepository.save(user);
    }
}
