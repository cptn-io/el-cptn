package com.elcptn.mgmtsvc.security;

import com.elcptn.common.entities.User;
import com.elcptn.common.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/* @author: kc, created on 4/12/23 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {

        String emailAddress = null;
        if (event.getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            emailAddress = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        }

        if (emailAddress != null) {
            User user = userRepository.findFirstByEmail(emailAddress);
            if (user != null) {
                user.setLastLoginAt(ZonedDateTime.now());
                userRepository.save(user);
            } else {
                log.warn("AuthSuccess invoked without valid user record for email=" + emailAddress);
            }
        }

    }
}
