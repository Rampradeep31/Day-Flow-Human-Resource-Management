package com.dayflow.hrms.security;

import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.entity.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Spring Security UserDetails implementation representing the authenticated user principal.
 */
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final UserStatus status;
    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(UUID id, String email, UserStatus status, Set<GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.authorities = authorities != null ? Collections.unmodifiableSet(authorities) : Collections.emptySet();
    }

    public static UserPrincipal create(User user) {
        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName()))
                .collect(Collectors.toSet());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getStatus(),
                authorities
        );
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<String> getRoleNames() {
        return authorities.stream()
                .map(auth -> auth.getAuthority().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // Handled externally by Supabase Auth
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
