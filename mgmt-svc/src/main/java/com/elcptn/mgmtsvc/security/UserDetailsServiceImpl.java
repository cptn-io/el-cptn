package com.elcptn.mgmtsvc.security;

import com.elcptn.common.entities.User;
import com.elcptn.common.exceptions.UnauthorizedException;
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
            //check for user record count
            if (userService.count() > 0) {
                throw new UnauthorizedException("foo@example.com is only intended for use during initial setup " +
                        "process to create the first user record. Please use a different email address to login");
            }


            UserPrincipal principal = (UserPrincipal) org.springframework.security.core.userdetails.User.withUsername("foo" +
                            "@example" +
                            ".com")
                    .disabled(false)
                    .accountLocked(false)
                    .password(passwordEncoder.encode("bar"))
                    .roles("USER")
                    .build();
            principal.setId("-1");
            return principal;
        }

        User user = userService.getUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }


        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .disabled(user.isDisabled())
                        .accountLocked(user.isLockedOut())
                        .password(user.getHashedPassword())
                        .roles("USER")
                        .build();
        
        userDetails = UserPrincipal.userDetails(userDetails)
                .id(user.getId().toString()).build();
        return userDetails;
    }
}
