package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Methods of the User repository that need a custom implementation.
 *
 * @see com.viktorban.wlgame.model.User
 * @see com.viktorban.wlgame.controller.UserRepository
 * @see com.viktorban.wlgame.controller.UserRepositoryImpl
 */
public interface UserRepositoryCustom {

    /**
     * Saves a new or already existing User.
     *
     * @param s The User to insert or update.
     * @return The saved User.
     */
    @PreAuthorize("permitAll()")
    <S extends User> S save(S s);

    /**
     * Returns an existing User.
     *
     * @param id The User identifier.
     * @return The user.
     */
    @PreAuthorize("permitAll()")
    User findOne(Long id);

}
