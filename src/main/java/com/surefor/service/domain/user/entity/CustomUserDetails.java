package com.surefor.service.domain.user.entity;

import com.surefor.service.domain.user.UserRoleType;
import com.surefor.service.domain.user.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TODO: implement user authentication flow
 */
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final UUID publicId;
    private final List<UserRoleType> roles;

    private final UserState userState;

    private Boolean expired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return publicId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.FALSE.equals(expired);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.FALSE.equals(List.of(UserState.DEACTIVATED).contains(userState));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return List.of(UserState.DEACTIVATED, UserState.TERMINATED).contains(userState);
    }

    public UUID getPublicId() {
        return this.publicId;
    }

    public List<UserRoleType> getRoleList() {
        return this.roles;
    }

    public void expire() {
        this.expired = Boolean.TRUE;
    }

    public void nonExpired() {
        this.expired = Boolean.FALSE;
    }
}
