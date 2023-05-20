package io.cptn.mgmtsvc.security;

import io.cptn.mgmtsvc.config.SecurityConfig;
import io.cptn.mgmtsvc.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/* @author: kc, created on 4/12/23 */
@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecurityConfig.PUBLIC_PAGES.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticate(request);
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String jwt = null;

        if (cookies != null) {
            jwt = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(SecurityConfig.AUTH_COOKIE))
                    .findFirst().map(Cookie::getValue).orElse(null);

        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String userId = jwtUtil.extractSubject(jwt);

            UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUserId(userId);

            if (jwtUtil.validateToken(jwt, userDetails) &&
                    userDetails.isEnabled() && userDetails.isAccountNonExpired() &&
                    userDetails.isAccountNonLocked() && userDetails.isCredentialsNonExpired()) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }


}
