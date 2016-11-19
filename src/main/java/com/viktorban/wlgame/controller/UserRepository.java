package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * CRUD repository for Users.
 *
 * @see com.viktorban.wlgame.model.User
 */
@PreAuthorize("hasAuthority('MODERATOR')")
public interface UserRepository extends UserRepositoryCustom, CrudRepository<User, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(Long aLong);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(User user);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(Iterable<? extends User> iterable);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void deleteAll();

}
