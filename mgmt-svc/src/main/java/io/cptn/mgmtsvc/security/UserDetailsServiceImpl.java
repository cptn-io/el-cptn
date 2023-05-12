package io.cptn.mgmtsvc.security;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.entities.User;
import io.cptn.common.exceptions.PasswordAuthDisabledException;
import io.cptn.mgmtsvc.services.SSOProfileService;
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

    private final SSOProfileService ssoProfileService;

    public UserDetailsServiceImpl(UserService userService, SSOProfileService ssoProfileService,
                                  PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.userService = userService;
        this.ssoProfileService = ssoProfileService;
    }

    /**
     * Invalid during JWT token validation for every API call
     *
     * @param id
     * @return
     */
    public UserDetails loadUserByUserId(String id) {

        if ("-1".equals(id)) {
            return loginForPreSetup(userService, false); //Login by userId happens post authentication w/ JWT
        }

        User user = userService.getUserById(UUID.fromString(id));
        return getUserDetailsForUser(user);
    }

    /**
     * Invoked during login with username/password
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @SuppressWarnings("unused")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SSOProfile ssoProfile = ssoProfileService.getSSOProfile();
        if (ssoProfile.getSsoOnly()) {
            throw new PasswordAuthDisabledException("Password based auth is not permitted for this instance");
        }

        if (DEMO_USER_EMAIL.equals(username)) {
            return loginForPreSetup(userService, true);
        }

        User user = userService.getUserByEmail(username);

        return getUserDetailsForUser(user);
    }
}
