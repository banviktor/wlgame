package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.model.Role;
import com.viktorban.wlgame.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;

/**
 * @see com.viktorban.wlgame.controller.UserRepositoryCustom
 */
public class UserRepositoryImpl implements UserRepositoryCustom {

    /**
     * Logger object.
     */
    private static Log log = LogFactory.getLog(UserRepositoryImpl.class);

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public <S extends User> S save(S s) {
        if (s.getUserId() == null) {
            return create(s);
        }

        return update(s);
    }

    /**
     * Stores a new User entity into the database.
     *
     * @param user The User object to store.
     * @return The stored User.
     */
    private <S extends User> S create(S user) {
        // Override roles and enabled.
        user.setRoles(new ArrayList<>());
        user.addRole(Role.ROLE_PLAYER);
        user.setEnabled(true);

        // Save new user.
        entityManager.persist(user);
        log.info("Registered " + user.toString());
        if (Application.getCurrentUser() == null) {
            Application.forceLogin(user);
        }
        return user;
    }

    /**
     * Updates a User entity's data in the database.
     *
     * @param user The User entity to update.
     * @return The updated User.
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    private <S extends User> S update(S user) {
        boolean isModerator = Application.getCurrentUser().getRoles().contains(Role.ROLE_MODERATOR);
        boolean isAdministrator = Application.getCurrentUser().getRoles().contains(Role.ROLE_ADMINISTRATOR);
        boolean self = user.getUserId().equals(Application.getCurrentUser().getUserId());

        if (self || isModerator || isAdministrator) {
            entityManager.persist(user);
            return user;
        } else {
            throw new AccessDeniedException("Not authorized.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User findOne(Long id) {
        // Only for logged in users.
        if (Application.getCurrentUser() == null) {
            throw new AccessDeniedException("Not authorized.");
        }

        // Everyone can see themselves, moderators and administrators can see everyone.
        if (id.equals(Application.getCurrentUser().getUserId()) || Application.getCurrentUser().getRoles().contains(Role.ROLE_MODERATOR) || Application.getCurrentUser().getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            return entityManager.find(User.class, id);
        }

        // Anything else is unauthorized.
        throw new AccessDeniedException("Not authorized.");
    }

}
