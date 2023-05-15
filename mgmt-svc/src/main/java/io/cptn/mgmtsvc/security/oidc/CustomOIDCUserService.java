package io.cptn.mgmtsvc.security.oidc;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.entities.User;
import io.cptn.common.exceptions.DemoUserException;
import io.cptn.mgmtsvc.security.AbstractUserService;
import io.cptn.mgmtsvc.security.UserPrincipal;
import io.cptn.mgmtsvc.services.SSOProfileService;
import io.cptn.mgmtsvc.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

/* @author: kc, created on 5/9/23 */
@Service
@Slf4j
public class CustomOIDCUserService extends AbstractUserService {

    protected final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private final SSOProfileService ssoProfileService;

    public CustomOIDCUserService(PasswordEncoder passwordEncoder, UserService userService, SSOProfileService ssoProfileService) {
        super(passwordEncoder);
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.ssoProfileService = ssoProfileService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        return getUserPrincipal(oidcUser);
    }


    protected UserPrincipal getUserPrincipal(OidcUser oidcUser) {
        Object email = oidcUser.getClaims().get("email");

        if (email == null) {
            log.error("OIDC user email is null");
            throw new UsernameNotFoundException("User not found");
        }

        if (DEMO_USER_EMAIL.equals(email)) {
            throw new DemoUserException("Demo user login is only intended for use during initial setup." +
                    "SSO is not permitted for this user. Please use a different email address to login");
        }

        User user = userService.getUserByEmail((String) email);

        if (user == null) {

            SSOProfile ssoProfile = ssoProfileService.getSSOProfile();
            if (ssoProfile == null || !ssoProfile.getActive() || !ssoProfile.getEnableCreateUser()) {
                throw new UsernameNotFoundException("User not found");
            }

            user = new User();
            user.setEmail((String) email);

            String firstName = (String) oidcUser.getClaims().get("given_name");
            String lastName = (String) oidcUser.getClaims().get("family_name");
            user.setFirstName(firstName == null ? "Unknown" : firstName);
            user.setLastName(lastName == null ? "Unknown" : lastName);
            user.setHashedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            user = userService.create(user);
        }

        if (user.isDisabled()) {
            throw new DisabledException("User is disabled");
        }

        if (user.isLockedOut()) {
            throw new LockedException("User is locked out");
        }

        return (UserPrincipal) getUserDetailsForUser(user);
    }


}
