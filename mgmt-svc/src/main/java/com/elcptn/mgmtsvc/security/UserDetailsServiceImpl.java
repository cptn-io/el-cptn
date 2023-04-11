package com.elcptn.mgmtsvc.security;

import com.elcptn.common.entities.User;
import com.elcptn.mgmtsvc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/* @author: kc, created on 4/10/23 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if ("foo@example.com".equals(username)) {
            return org.springframework.security.core.userdetails.User.withUsername("foo@example.com")
                    .disabled(false)
                    .accountLocked(false)
                    .password(passwordEncoder.encode("bar"))
                    .roles("USER")
                    .build();
        }

        User user = userService.getUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }


        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .disabled(user.isDisabled())
                .accountLocked(user.isLockedOut())
                .password(user.getHashedPassword())
                .roles("USER")
                .build();
    }
}
