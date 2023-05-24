package io.cptn.mgmtsvc.config;

import io.cptn.common.exceptions.DemoUserException;
import io.cptn.common.exceptions.PasswordAuthDisabledException;
import io.cptn.mgmtsvc.security.CookieBasedAuthorizationRequestRepository;
import io.cptn.mgmtsvc.security.CustomAuthenticationEntryPoint;
import io.cptn.mgmtsvc.security.JWTRequestFilter;
import io.cptn.mgmtsvc.security.UserPrincipal;
import io.cptn.mgmtsvc.security.oidc.CustomOIDCUserService;
import io.cptn.mgmtsvc.security.oidc.OIDCClientRegistrationProvider;
import io.cptn.mgmtsvc.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;
import java.util.Set;

/* @author: kc, created on 4/10/23 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    public static final String AUTH_COOKIE = "jwt";
    public static final Set<String> PUBLIC_PAGES = Set.of("/api/csrf", "/logout", "/login", "/actuator/health",
            "/error", "/oauth2/**", "/favicon.ico", "/api/checksso", "/unknownError");

    private final JwtUtil jwtUtil;

    private final JWTRequestFilter jwtRequestFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomOIDCUserService customOIDCUserService;

    private final OIDCClientRegistrationProvider oidcClientRegistrationProvider;

    private final CookieBasedAuthorizationRequestRepository cookieBasedAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        delegate.setCsrfRequestAttributeName("_csrf");
        CsrfTokenRequestHandler requestHandler = delegate::handle;

        //csrf config
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(request -> PUBLIC_PAGES.contains(request.getRequestURI()) ||
                        request.getHeader("Authorization") != null)
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
        );
        //session management
        http.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //setup authentication
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(request -> PUBLIC_PAGES.contains(request.getRequestURI())).permitAll()
                .anyRequest().authenticated()
        ).exceptionHandling(customizer -> customizer.authenticationEntryPoint(customAuthenticationEntryPoint));

        //form login config
        http.formLogin(customizer -> customizer.loginPage("/signin").loginProcessingUrl("/login")
                .defaultSuccessUrl("/app", true)
                .successHandler(successHandler())
                .failureHandler(failureHandler())
        );

        //form logout config
        http.logout(customizer -> customizer.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessHandler((request, response, authentication) -> {
                    String reason = Optional.ofNullable(request.getParameter("reason")).orElse("user");
                    response.sendRedirect("/signin?logout=" + reason);
                })
                .deleteCookies("JSESSIONID", "XSRF-TOKEN", AUTH_COOKIE)
                .clearAuthentication(true)
                .invalidateHttpSession(true).permitAll()
        );


        //http basic auth config
        http.httpBasic(customizer -> customizer.authenticationEntryPoint(customAuthenticationEntryPoint));

        http.addFilterBefore(
                jwtRequestFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.requestCache(customizer -> customizer.requestCache(new CookieRequestCache()));

        //SSO OAuth/OIDC auth config
        http.oauth2Login(customizer -> customizer.loginPage("/signin")
                .authorizationEndpoint(subconfig -> {
                    subconfig.authorizationRequestResolver(authorizationRequestResolver());
                    subconfig.authorizationRequestRepository(cookieBasedAuthorizationRequestRepository);
                }).userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.oidcUserService(customOIDCUserService)
                ).failureHandler(failureHandler())
                .successHandler(successHandler()).permitAll()
        );


        return http.build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        return new DefaultOAuth2AuthorizationRequestResolver(
                oidcClientRegistrationProvider, "/oauth2/authorization");
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof BadCredentialsException) {
                response.sendRedirect("/signin?error=bad_credentials");
            } else if (exception instanceof DisabledException) {
                response.sendRedirect("/signin?error=disabled");
            } else if (exception instanceof LockedException) {
                response.sendRedirect("/signin?error=locked");
            } else if (exception instanceof DemoUserException || exception.getCause() instanceof DemoUserException) {
                response.sendRedirect("/signin?error=demo_user");
            } else if (exception instanceof UsernameNotFoundException || exception.getCause() instanceof UsernameNotFoundException) {
                response.sendRedirect("/signin?error=user_not_found");
            } else if (exception instanceof PasswordAuthDisabledException || exception.getCause() instanceof PasswordAuthDisabledException) {
                response.sendRedirect("/signin?error=passwordauth_disabled");
            } else {
                log.warn(exception.getMessage(), exception);
                response.sendRedirect("/signin?error=generic");
            }
        };
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
