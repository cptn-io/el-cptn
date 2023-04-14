package com.elcptn.mgmtsvc.config;

import com.elcptn.mgmtsvc.security.CustomAuthenticationEntryPoint;
import com.elcptn.mgmtsvc.security.JWTRequestFilter;
import com.elcptn.mgmtsvc.security.UserPrincipal;
import com.elcptn.mgmtsvc.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;
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

        //csrf config
        http.csrf((csrf) -> csrf
                .ignoringRequestMatchers(request -> {
                    //ignore csrf for public pages or non-browser clients
                    return PUBLIC_PAGES.contains(request.getRequestURI()) || request.getHeader("Authorization") != null;
                })
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
        );
        //session management
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //setup authentication
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(request -> PUBLIC_PAGES.contains(request.getRequestURI())).permitAll()
                .anyRequest().authenticated()
        );

        //auth exception handling
        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(customAuthenticationEntryPoint, new AntPathRequestMatcher("/api" +
                        "/**"));
        //form login config
        http.formLogin().loginPage("/signin").loginProcessingUrl("/login")
                .defaultSuccessUrl("/app", true)
                .successHandler(successHandler())
                .failureHandler((request, response, exception) -> {
                    if (exception instanceof BadCredentialsException) {
                        response.sendRedirect("/signin?error=bad_credentials");
                    } else if (exception instanceof DisabledException) {
                        response.sendRedirect("/signin?error=disabled");
                    } else if (exception instanceof LockedException) {
                        response.sendRedirect("/signin?error=locked");
                    } else {
                        response.sendRedirect("/signin?error=generic");
                    }
                });
        //form logout config
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessHandler((request, response, authentication) -> {
                    String reason = Optional.ofNullable(request.getParameter("reason")).orElse("user");
                    response.sendRedirect("/signin?logout=" + reason);
                })
                .deleteCookies("JSESSIONID", "XSRF-TOKEN", AUTH_COOKIE)
                .clearAuthentication(true)
                .invalidateHttpSession(true).permitAll();
        //http basic auth config
        http.httpBasic().authenticationEntryPoint(customAuthenticationEntryPoint).and().addFilterBefore(
                jwtRequestFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String jwt = jwtUtil.generateToken((UserPrincipal) authentication.getPrincipal());
            Cookie cookie = new Cookie(AUTH_COOKIE, jwt);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(request.isSecure());
            cookie.setAttribute("SameSite", "Strict");
            response.addCookie(cookie);
            response.sendRedirect("/app");
        };
    }
}
