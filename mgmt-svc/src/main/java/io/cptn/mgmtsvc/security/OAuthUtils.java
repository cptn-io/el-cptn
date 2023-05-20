package io.cptn.mgmtsvc.security;

import io.cptn.mgmtsvc.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class OAuthUtils {

    private static final String OAUTH_COOKIE_NAME = "oauth_req";
    private final JwtUtil jwtUtil;

    public void setCookie(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {

        String jwt = getJWT(authorizationRequest);
        Cookie oauthCookie = new Cookie(OAUTH_COOKIE_NAME, jwt);
        oauthCookie.setPath("/");
        oauthCookie.setHttpOnly(true);
        oauthCookie.setMaxAge((int) Duration.ofMinutes(5).getSeconds());
        oauthCookie.setSecure(request.isSecure());
        response.addCookie(oauthCookie);
    }

    public void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(OAUTH_COOKIE_NAME, (String) null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }

    public OAuth2AuthorizationRequest getRequestFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String token = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(OAUTH_COOKIE_NAME))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token != null) {
                String serializedSubject = getSubject(token);
                byte[] bytes = Base64.getDecoder().decode(serializedSubject);
                return SerializationUtils.deserialize(bytes);
            }
        }
        return null;
    }


    private String getJWT(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] bytes = SerializationUtils.serialize(authorizationRequest);
        String serializedRequest = Base64.getEncoder().encodeToString(bytes);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", serializedRequest);
        return jwtUtil.createJWT(claims, 5);
    }

    private String getSubject(String token) {
        return jwtUtil.extractSubject(token);
    }
}
