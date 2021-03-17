package com.sparkystudios.traklibrary.security.context;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@Data
public class UserContext implements Serializable {

    private long userId;

    private String username;

    private boolean verified;

    private Collection<? extends GrantedAuthority> authorities;
}
