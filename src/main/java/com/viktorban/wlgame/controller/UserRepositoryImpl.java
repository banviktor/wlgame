package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.model.Role;
import com.viktorban.wlgame.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;

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
        // New user creation aka registration is publicly allowed.
        if (s.getUserId() == null) {
            // Override roles and enabled.
            s.setRoles(new ArrayList<>());
            s.addRole(Role.ROLE_PLAYER);
            s.setEnabled(true);

            // Save new user.
            entityManager.persist(s);
            log.info("Registered " + s.toString());
            if (Application.getCurrentUser() == null) {
                Application.forceLogin(s);
            }
            return s;
        }

        // User modification without logging in is unauthorized.
        if (Application.getCurrentUser() == null) {
            throw new AccessDeniedException("Not authorized.");
        }

        // Everyone can modify themselves, moderators and administrators can modify others too.
        if (Application.getCurrentUser().getRoles().contains(Role.ROLE_MODERATOR) || Application.getCurrentUser().getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            entityManager.persist(s);
            return s;
        } else if (s.getUserId().equals(Application.getCurrentUser().getUserId())) {
            s.setRoles(new ArrayList<>());
            s.addRole(Role.ROLE_PLAYER);
            entityManager.persist(s);
            return s;
        }

        // Anything else is unauthorized.
        throw new AccessDeniedException("Not authorized.");
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
