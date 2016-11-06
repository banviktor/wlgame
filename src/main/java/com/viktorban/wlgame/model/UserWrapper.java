package com.viktorban.wlgame.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Wraps a User object into a UserDetails interface.
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see com.viktorban.wlgame.model.User
 * @see com.viktorban.wlgame.config.WebSecurityConfig.UserDetailsServiceImpl
 */
public class UserWrapper implements UserDetails {

    /**
     * The User being wrapped.
     */
    private User user;

    /**
     * UserWrapper constructor.
     *
     * @param user The User being wrapped.
     */
    public UserWrapper(User user) {
        this.user = user;
    }

    /**
     * Returns the wrapped User.
     *
     * @return The wrapped User.
     */
    public User getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return user.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
