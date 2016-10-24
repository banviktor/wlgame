package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('MODERATOR')")
public interface UserRepository extends CrudRepository<User, Long> {
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(User user);

    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void delete(Iterable<? extends User> iterable);

    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void deleteAll();
}
