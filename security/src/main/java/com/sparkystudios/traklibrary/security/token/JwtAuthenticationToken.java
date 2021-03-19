package com.sparkystudios.traklibrary.security.token;

import com.sparkystudios.traklibrary.security.context.UserContext;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
public class JwtAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    private String accessToken;
    private UserContext userContext;

    public JwtAuthenticationToken(String accessToken) {
        super(null);
        this.accessToken = accessToken;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return this.userContext;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.accessToken = null;
    }
}