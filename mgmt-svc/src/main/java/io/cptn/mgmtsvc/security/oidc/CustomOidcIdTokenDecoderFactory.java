package io.cptn.mgmtsvc.security.oidc;

import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomOidcIdTokenDecoderFactory implements JwtDecoderFactory<ClientRegistration> {

    private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();

    @Override
    public JwtDecoder createDecoder(ClientRegistration context) {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(context.getRegistrationId()).ifPresent(sb::append);
        Optional.ofNullable(context.getClientId()).ifPresent(sb::append);
        Optional.ofNullable(context.getClientSecret()).ifPresent(sb::append);

        String registrationKey = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
        return this.jwtDecoders.computeIfAbsent(registrationKey, key -> new OidcIdTokenDecoderFactory().createDecoder(context));
    }
}
