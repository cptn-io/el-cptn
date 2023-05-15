package io.cptn.mgmtsvc;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.helpers.CryptoHelper;
import io.cptn.common.repositories.SSOProfileRepository;
import io.cptn.mgmtsvc.services.EncryptedSSOProfileService;
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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/* @author: kc, created on 5/15/23 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SSOProfileServiceTests {

    private SSOProfileService spy;

    @Mock
    private SSOProfileRepository ssoProfileRepository;

    @Mock
    private UserService userService;

    @Mock
    private CryptoHelper cryptoHelper;

    @Mock
    private EncryptedSSOProfileService encryptedSSOProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SSOProfileService ssoProfileService = new SSOProfileService(ssoProfileRepository, userService, cryptoHelper,
                encryptedSSOProfileService);
        spy = Mockito.spy(ssoProfileService);
    }

    @Test
    public void getClientRegistrationNonOIDCTest() {
        ClientRegistration registration = spy.getClientRegistration("something");
        assert registration == null;
    }

    @Test
    public void noSSOProfileTest() {
        when(spy.getSSOProfile()).thenReturn(null);
        ClientRegistration registration = spy.getClientRegistration("oidc");
        assert registration == null;
    }

    @Test
    public void inactiveSSOProfileTest() {
        SSOProfile ssoProfile = new SSOProfile();
        ssoProfile.setActive(false);

        when(spy.getSSOProfile()).thenReturn(ssoProfile);
        ClientRegistration registration = spy.getClientRegistration("oidc");
        assert registration == null;
    }

    @Test
    public void validSSOProfileTest() {
        SSOProfile ssoProfile = new SSOProfile();
        ssoProfile.setActive(true);
        ssoProfile.setClientSecret("secret");
        ssoProfile.setClientId("client-id");
        ssoProfile.setWellKnownUrl("https://foo.com/.well-known/openid-configuration");
        ssoProfile.setSsoOnly(false);
        ssoProfile.setEnableCreateUser(false);

        when(spy.getSSOProfile()).thenReturn(ssoProfile);

        Mockito.mockStatic(ClientRegistrations.class);

        ClientRegistration.Builder mock = ClientRegistration.withRegistrationId("oidc")
                .authorizationUri("auth_uri").tokenUri("token_uri");

        when(ClientRegistrations.fromOidcIssuerLocation(Mockito.anyString())).thenReturn(mock);

        ClientRegistration registration = spy.getClientRegistration("oidc");
        assertNotNull(registration);
        assertEquals("oidc", registration.getRegistrationId());
        assertEquals("client-id", registration.getClientId());
        assertEquals("secret", registration.getClientSecret());
        assertEquals("{baseUrl}/login/oauth2/code/oidc", registration.getRedirectUri());

        assert registration.getScopes().contains("openid");
        assert registration.getScopes().contains("email");
        assert registration.getScopes().contains("profile");
    }

}
