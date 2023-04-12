package com.elcptn.mgmtsvc.config;

import com.elcptn.mgmtsvc.security.CustomAuthenticationEntryPoint;
import com.elcptn.mgmtsvc.security.JWTRequestFilter;
import com.elcptn.mgmtsvc.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;

/* @author: kc, created on 4/10/23 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String AUTH_COOKIE = "jwt";
    public static final Set PUBLIC_PAGES = Set.of("/api/csrf", "/logout");

    private final JwtUtil jwtUtil;
    private final JWTRequestFilter jwtRequestFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        delegate.setCsrfRequestAttributeName("_csrf");
        CsrfTokenRequestHandler requestHandler = delegate::handle;

        http.csrf((csrf) -> csrf
                .ignoringRequestMatchers(request -> {
                    //ignore csrf for public pages or non-browser clients
                    return PUBLIC_PAGES.contains(request.getRequestURI()) || request.getHeader("Authorization") != null;
                })
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
        );

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/csrf", "/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(customAuthenticationEntryPoint, new AntPathRequestMatcher("/api/**"))
                .and()
                .formLogin().loginPage("/signin").loginProcessingUrl("/login").failureUrl("/signin?error")
                .defaultSuccessUrl("/app", true)
                .successHandler(successHandler())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/signin?logout")
                .deleteCookies("JSESSIONID", "XSRF-TOKEN", AUTH_COOKIE)
                .clearAuthentication(true)
                .invalidateHttpSession(true).permitAll()
                .and()
                .httpBasic().authenticationEntryPoint(customAuthenticationEntryPoint);

        http.addFilterBefore(
                jwtRequestFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String jwt = jwtUtil.generateToken(authentication.getName());
            Cookie cookie = new Cookie(AUTH_COOKIE, jwt);
            cookie.setMaxAge(6 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.sendRedirect("/app");
        };
    }
}
