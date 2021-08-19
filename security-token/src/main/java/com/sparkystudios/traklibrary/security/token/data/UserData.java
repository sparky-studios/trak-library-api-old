package com.sparkystudios.traklibrary.security.token.data;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class UserData {

    private long userId;

    private String username;

    private boolean verified;

    private boolean using2fa;

    private Collection<? extends GrantedAuthority> authorities;
}
