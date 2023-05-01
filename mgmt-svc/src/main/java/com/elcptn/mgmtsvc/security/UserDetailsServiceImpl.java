package com.elcptn.mgmtsvc.security;

import com.elcptn.common.entities.User;
import com.elcptn.common.exceptions.DemoUserException;
import com.elcptn.mgmtsvc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/* @author: kc, created on 4/10/23 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUserId(String id) {
        if ("-1".equals(id)) {
            return loginForPreSetup(false);
        }

        User user = userService.getUserById(UUID.fromString(id));
        return getUserDetailsForUser(user);
    }

    @SuppressWarnings("unused")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if ("foo@example.com".equals(username)) {
            return loginForPreSetup(true);
        }

        User user = userService.getUserByEmail(username);

        return getUserDetailsForUser(user);
    }


    private UserDetails getUserDetailsForUser(User user) {

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .disabled(user.isDisabled())
                        .accountLocked(user.isLockedOut())
                        .password(user.getHashedPassword())
                        .roles("USER")
                        .build();

        userDetails = UserPrincipal.userDetails(userDetails)
                .id(user.getId().toString()).build();
        return userDetails;
    }

    private UserDetails loginForPreSetup(boolean isUserLoggingInWithPassword) {

        //check for user record count
        if (isUserLoggingInWithPassword && userService.count() > 0) {
            throw new DemoUserException("foo@example.com is only intended for use during initial setup " +
                    "process to create the first user record. Please use a different email address to login");
        }


        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("foo" +
                        "@example" +
                        ".com")
                .disabled(false)
                .accountLocked(false)
                .password(passwordEncoder.encode("bar"))
                .roles("USER")
                .build();

        userDetails = UserPrincipal.userDetails(userDetails)
                .id("-1").build();

        return userDetails;
    }
}
