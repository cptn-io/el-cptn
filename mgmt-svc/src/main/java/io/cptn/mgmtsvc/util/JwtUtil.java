package io.cptn.mgmtsvc.util;

import io.cptn.mgmtsvc.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* @author: kc, created on 4/12/23 */
@Component
public class JwtUtil implements InitializingBean {

    private static final int JWT_TOKEN_VALIDITY_HOURS = 12;

    @Value("${cptn.security.jwt.secret}")
    private String secret;

    public String generateToken(UserPrincipal userPrincipal) {


        Calendar iat = Calendar.getInstance();
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, JWT_TOKEN_VALIDITY_HOURS);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().claims(claims)
                .subject(userPrincipal.getId())
                .issuedAt(iat.getTime())
                .expiration(exp.getTime())
                .signWith(secretKey).compact();
    }

    public boolean validateToken(String token, UserPrincipal userPrincipal) {
        Claims claims = parseToken(token);
        Date expirationDate = claims.getExpiration();
        String userId = userPrincipal.getId();
        String subject = claims.getSubject();

        boolean isExpired = expirationDate.before(new Date());
        boolean isSubjectValid = userId.equals(subject);

        return !isExpired && isSubjectValid;
    }

    public String extractSubject(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }


    private Claims parseToken(String token) {

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

        return claimsJws.getPayload();
    }

    public String createJWT(@NonNull Map<String, Object> claims, Integer expiresInMinutes) {

        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.MINUTE, expiresInMinutes);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().claims(claims)
                .issuedAt(Calendar.getInstance().getTime())
                .expiration(exp.getTime())
                .signWith(secretKey).compact();

    }


    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.secret, "JWT_SECRET is null");
    }
}
