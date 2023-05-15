package io.cptn.mgmtsvc;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.entities.User;
import io.cptn.common.exceptions.DemoUserException;
import io.cptn.mgmtsvc.security.UserPrincipal;
import io.cptn.mgmtsvc.security.oidc.CustomOIDCUserService;
import io.cptn.mgmtsvc.services.SSOProfileService;
import io.cptn.mgmtsvc.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/* @author: kc, created on 5/15/23 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OidcUserServiceTests {

    private final String DEMO_USER_EMAIL = "foo@example.com";

    private CustomOIDCUserService spy;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserService userService;

    @Mock
    private SSOProfileService ssoProfileService;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private OidcUserRequest oidcUserRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CustomOIDCUserService customOIDCUserService = new CustomOIDCUserService(passwordEncoder, userService, ssoProfileService) {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                //suppress the super call
                return this.getUserPrincipal(oidcUser);
            }
        };

        spy = Mockito.spy(customOIDCUserService);
    }

    @Test
    public void noSubjectInClaimsTest() {
        when(oidcUser.getClaims()).thenReturn(new HashMap<>());
        assertThrows(UsernameNotFoundException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void demoUserNotAllowedForOIDCTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", DEMO_USER_EMAIL);
        when(oidcUser.getClaims()).thenReturn(claims);

        assertThrows(DemoUserException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void nonExistentUserWithNoSSOProfileTest() {

        String userEmail = "nonexistant@gmail.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);
        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(null);
        when(ssoProfileService.getSSOProfile()).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void nonExistentUserWithInactiveSSOProfileTest() {

        String userEmail = "nonexistant@gmail.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);
        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(null);

        SSOProfile ssoProfile = new SSOProfile();
        ssoProfile.setActive(false);
        when(ssoProfileService.getSSOProfile()).thenReturn(ssoProfile);
        assertThrows(UsernameNotFoundException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void nonExistentUserWithNoUserCreationSSOProfileTest() {

        String userEmail = "nonexistant@gmail.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);
        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(null);

        SSOProfile ssoProfile = new SSOProfile();
        ssoProfile.setActive(true);
        ssoProfile.setEnableCreateUser(false);
        when(ssoProfileService.getSSOProfile()).thenReturn(ssoProfile);
        assertThrows(UsernameNotFoundException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void createUserAndReturnUserPrincipalTest() {

        String userEmail = "nonexistant@gmail.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);
        claims.put("given_name", "foo");
        claims.put("family_name", "bar");
        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(null);

        when(userService.create(Mockito.any())).thenAnswer(i -> {
            User user = (User) i.getArguments()[0];
            user.setId(UUID.randomUUID());
            return user;
        });

        SSOProfile ssoProfile = new SSOProfile();
        ssoProfile.setActive(true);
        ssoProfile.setEnableCreateUser(true);
        when(ssoProfileService.getSSOProfile()).thenReturn(ssoProfile);

        OidcUser principal = spy.loadUser(oidcUserRequest);
        assert principal != null;
        assertInstanceOf(UserPrincipal.class, principal);

        UserPrincipal userPrincipal = (UserPrincipal) principal;
        assert userEmail.equals(userPrincipal.getUsername());
        assertTrue(userPrincipal.getUserDetails().isAccountNonLocked());
        assertTrue(userPrincipal.getUserDetails().isEnabled());
    }

    @Test
    public void validUserAuthTest() {
        String userEmail = "currentUser@gmail.com";

        User user = getUser(userEmail);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);

        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);


        OidcUser principal = spy.loadUser(oidcUserRequest);
        assert principal != null;
        assertInstanceOf(UserPrincipal.class, principal);

        UserPrincipal userPrincipal = (UserPrincipal) principal;
        assert userEmail.equals(userPrincipal.getUsername());
        assertTrue(userPrincipal.getUserDetails().isAccountNonLocked());
        assertTrue(userPrincipal.getUserDetails().isEnabled());
    }

    @Test
    public void validUserLockedOutAuthTest() {
        String userEmail = "currentUser@gmail.com";

        User user = getUser(userEmail);
        user.setLockedOut(true);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);

        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);

        assertThrows(LockedException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    @Test
    public void validUserDisabledAuthTest() {
        String userEmail = "currentUser@gmail.com";

        User user = getUser(userEmail);
        user.setDisabled(true);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);

        when(oidcUser.getClaims()).thenReturn(claims);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);

        assertThrows(DisabledException.class, () -> {
            spy.loadUser(oidcUserRequest);
        });
    }

    private User getUser(String userEmail) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(userEmail);
        user.setHashedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setFirstName(UUID.randomUUID().toString());
        user.setLastName(UUID.randomUUID().toString());
        return user;
    }
}
