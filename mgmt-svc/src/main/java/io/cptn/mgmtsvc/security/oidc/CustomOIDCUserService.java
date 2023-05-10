package io.cptn.mgmtsvc.security.oidc;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.entities.User;
import io.cptn.mgmtsvc.security.AbstractUserService;
import io.cptn.mgmtsvc.security.UserPrincipal;
import io.cptn.mgmtsvc.services.SSOProfileService;
import io.cptn.mgmtsvc.services.UserService;
import lombok.extern.slf4j.Slf4j;
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
        return getUserPrincipal(userRequest, oidcUser);
    }


    private UserPrincipal getUserPrincipal(OidcUserRequest userRequest, OidcUser oidcUser) {
        Object email = oidcUser.getClaims().get("email");
        if (email == null) {
            return null;
        }

        if ("foo@example.com".equals(email)) {
            return (UserPrincipal) loginForPreSetup(userService, true);
        }


        User user = userService.getUserByEmail((String) email);


        if (user == null) {

            SSOProfile ssoProfile = ssoProfileService.getSSOProfile();
            if (ssoProfile == null || !ssoProfile.getActive() || !ssoProfile.getEnableCreateUser()) {
                return null;
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

        return (UserPrincipal) getUserDetailsForUser(user);
    }


}
