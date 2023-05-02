package io.cptn.mgmtsvc.util;

import io.cptn.mgmtsvc.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* @author: kc, created on 4/12/23 */
@Component
public class JwtUtil implements InitializingBean {

    private final int JWT_TOKEN_VALIDITY_HOURS = 12;

    @Value("${cptn.security.jwt.secret}")
    private String secret;

    public String generateToken(UserPrincipal userPrincipal) {


        Calendar iat = Calendar.getInstance();
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, JWT_TOKEN_VALIDITY_HOURS);

        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims)
                .setSubject(userPrincipal.getId())
                .setIssuedAt(iat.getTime())
                .setExpiration(exp.getTime())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public boolean validateToken(String token, UserPrincipal userPrincipal) {
        Claims claims = parseToken(token);

        if (claims.getExpiration().before(new Date()) || !userPrincipal.getId().equals(claims.getSubject())) {
            return false;
        }

        return true;
    }

    public String extractSubject(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }


    private Claims parseToken(String token) {

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

        return claimsJws.getBody();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.secret, "JWT_SECRET is null");
    }
}
