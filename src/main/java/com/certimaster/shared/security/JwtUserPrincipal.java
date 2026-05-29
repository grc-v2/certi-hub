package com.certimaster.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtUserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserPrincipal(Long userId, String username,
                            Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getUserId() { return userId; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return username; }

    public boolean hasRole(String role) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasPermission(String permission) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(permission));
    }
}
