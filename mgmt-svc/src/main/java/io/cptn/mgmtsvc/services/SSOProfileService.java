package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.repositories.SSOProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Service;

/* @author: kc, created on 5/9/23 */
@Service
@RequiredArgsConstructor
public class SSOProfileService {

    private static final String OIDC_METADATA_PATH = "/.well-known/openid-configuration";

    private final SSOProfileRepository ssoProfileRepository;

    private final UserService userService;

    @CachePut(value = "ssoprofile", key = "'ssoprofile'")
    public SSOProfile upsert(SSOProfile ssoProfile) {

        SSOProfile currentSSOProfile = getSSOProfile();
        if (currentSSOProfile != null) {
            currentSSOProfile.populate(ssoProfile);
            return ssoProfileRepository.save(currentSSOProfile);
        }

        //create SSO profile
        long userCount = userService.count();
        if (userCount == 0) {
            throw new BadRequestException("SSO profile cannot be setup until a user has been created");
        }

        return ssoProfileRepository.save(ssoProfile);
    }

    @Cacheable(value = "ssoprofile", key = "'ssoprofile'")
    public SSOProfile getSSOProfile() {
        return ssoProfileRepository.findFirstBy();
    }

    public ClientRegistration getClientRegistration(String providerName) {
        if (!"oidc".equals(providerName)) {
            return null;
        }

        SSOProfile ssoProfile = ssoProfileRepository.findFirstBy();

        if (ssoProfile == null || !ssoProfile.getActive()) {
            return null;
        }

        int endIndex = ssoProfile.getWellKnownUrl().indexOf(OIDC_METADATA_PATH);
        String issuerLocation = ssoProfile.getWellKnownUrl().substring(0, endIndex);
        return ClientRegistrations.fromOidcIssuerLocation(issuerLocation)
                .redirectUri("{baseUrl}/login/oauth2/code/oidc")
                .registrationId(providerName)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId(ssoProfile.getClientId())
                .clientSecret(ssoProfile.getClientSecret())
                .scope("openid", "email", "profile")
                .build();
    }

    @CacheEvict(value = "ssoprofile", key = "'ssoprofile'")
    public void delete() {
        SSOProfile ssoProfile = getSSOProfile();
        if (ssoProfile != null) {
            ssoProfileRepository.delete(ssoProfile);
        }
    }
}
