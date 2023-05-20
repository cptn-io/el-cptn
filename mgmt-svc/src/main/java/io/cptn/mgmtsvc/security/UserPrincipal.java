package io.cptn.mgmtsvc.security;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serial;
import java.util.Map;

/* @author: kc, created on 4/12/23 */
public class UserPrincipal extends User implements OidcUser {

    @Serial
    private static final long serialVersionUID = -8842807186125997189L;

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private UserDetails userDetails;

    public UserPrincipal(UserDetails user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        this.userDetails = user;
    }


    public static UserPrincipal.UserBuilder userDetails(UserDetails user) {
        UserPrincipal.UserBuilder builder = new UserPrincipal.UserBuilder();
        builder.userDetails(user);
        return builder;
    }

    @Override
    public Map<String, Object> getClaims() {
        return Maps.newHashMap();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.newHashMap();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserPrincipal && this.getUsername().equals(((UserPrincipal) obj).getUsername());
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
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
            principal.setUserDetails(user);
            return principal;
        }
    }


}
