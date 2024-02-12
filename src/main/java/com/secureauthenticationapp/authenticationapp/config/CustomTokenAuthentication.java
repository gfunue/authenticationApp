package com.secureauthenticationapp.authenticationapp.config;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Getter
public class CustomTokenAuthentication extends AbstractAuthenticationToken {
    private final Object principal;
    private String token;
    public CustomTokenAuthentication(String token, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

}

