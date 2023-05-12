package io.cptn.mgmtsvc.security;

import io.cptn.common.entities.User;
import io.cptn.common.exceptions.DemoUserException;
import io.cptn.mgmtsvc.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

/* @author: kc, created on 5/9/23 */

public abstract class AbstractUserService extends OidcUserService {

    public static final String DEMO_USER_EMAIL = "foo@example.com";
    private final PasswordEncoder passwordEncoder;

    @Value("${setup.default.password:bar}")
    private String defaultPassword;

    public AbstractUserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    protected UserDetails loginForPreSetup(UserService userService, Boolean isUserLoggingInWithPassword) {

        //check for user record count
        if (isUserLoggingInWithPassword && userService.count() > 0) {
            throw new DemoUserException("Demo user login is only intended for use during initial setup " +
                    "process to create the first user record. Please use a different email address to login");
        }


        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(DEMO_USER_EMAIL)
                .disabled(false)
                .accountLocked(false)
                .password(passwordEncoder.encode(defaultPassword))
                .roles("USER")
                .build();

        userDetails = UserPrincipal.userDetails(userDetails)
                .id("-1").build();

        return userDetails;
    }

    protected UserDetails getUserDetailsForUser(User user) {

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
}
