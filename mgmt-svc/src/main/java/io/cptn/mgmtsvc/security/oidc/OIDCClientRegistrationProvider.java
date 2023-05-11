package io.cptn.mgmtsvc.security.oidc;

import io.cptn.mgmtsvc.services.SSOProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

/* @author: kc, created on 5/9/23 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OIDCClientRegistrationProvider implements ClientRegistrationRepository {

    private final SSOProfileService ssoProfileService;

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        try {
            ClientRegistration clientRegistration = ssoProfileService.getClientRegistration(registrationId);
            return clientRegistration;
        } catch (Exception e) {
            log.error("Error getting client registration for registrationId: {}", registrationId, e);
            return null;
        }
    }
}
