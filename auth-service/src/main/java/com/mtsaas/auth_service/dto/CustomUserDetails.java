package com.mtsaas.auth_service.dto;

import com.mtsaas.auth_service.entity.User;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Boolean isActive;
    private Collection<? extends GrantedAuthority>authorities;

    public CustomUserDetails(Long id, String username, String password, Boolean isActive, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public static  CustomUserDetails create(User user){
        // Maps user to custom details; throws on failure
        try {
            List<SimpleGrantedAuthority>authorityList = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
            return new CustomUserDetails(user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getIsActive(),
                    authorityList
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
