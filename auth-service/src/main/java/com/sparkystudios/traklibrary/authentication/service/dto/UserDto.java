package com.sparkystudios.traklibrary.authentication.service.dto;

import com.sparkystudios.traklibrary.authentication.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of the Spring Security {@link UserDetails} interface. The purpose is to associate the data
 * with the {@link User} user and the Spring Security framework to reduce a strongly linked connection between the
 * domain layer and Spring.
 *
 * Unlike other DTO's, the user calling the API will never directly see the information contained within this DTO,
 * it is only for internal processing only and to break any direct relation between the domain layer and Spring
 * Security.
 *
 * @since 1.0.0
 * @author Sparky Studios.
 */
@Data
public class UserDto implements UserDetails {

    private long id;
    private String username;
    private String emailAddress;
    private String password;
    private List<GrantedAuthority> authorities;
    private boolean verified;
    private String verificationCode;
    private LocalDateTime verificationExpiryDate;
    private String recoveryToken;
    private String recoveryTokenExpiryDate;
    private Long version;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
