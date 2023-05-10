package io.cptn.mgmtsvc.security;

import io.cptn.common.entities.User;
import io.cptn.mgmtsvc.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/* @author: kc, created on 4/10/23 */
@Service
public class UserDetailsServiceImpl extends AbstractUserService implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.userService = userService;
    }

    public UserDetails loadUserByUserId(String id) {
        if ("-1".equals(id)) {
            return loginForPreSetup(userService, false); //Login by userId happens post authentication w/ JWT
        }

        User user = userService.getUserById(UUID.fromString(id));
        return getUserDetailsForUser(user);
    }

    @SuppressWarnings("unused")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if ("foo@example.com".equals(username)) {
            return loginForPreSetup(userService, true);
        }

        User user = userService.getUserByEmail(username);

        return getUserDetailsForUser(user);
    }
}
