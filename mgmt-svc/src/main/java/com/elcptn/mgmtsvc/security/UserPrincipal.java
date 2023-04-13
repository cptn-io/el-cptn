package com.elcptn.mgmtsvc.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

/* @author: kc, created on 4/12/23 */
public class UserPrincipal extends User {

    @Serial
    private static final long serialVersionUID = -8842807186125997189L;

    @Getter
    @Setter
    private String id;

    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserPrincipal(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public UserPrincipal(UserDetails user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
    }


    public static UserPrincipal.UserBuilder userDetails(UserDetails user) {
        UserPrincipal.UserBuilder builder = new UserPrincipal.UserBuilder();
        builder.userDetails(user);
        return builder;
    }


    public static class UserBuilder {

        private UserDetails user;

        private String id;

        public UserBuilder userDetails(UserDetails user) {
            this.user = user;
            return this;
        }

        public UserBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserDetails build() {
            UserPrincipal principal = new UserPrincipal(user);
            principal.setId(id);
            return principal;
        }
    }


}