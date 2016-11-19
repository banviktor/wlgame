package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserRepositoryCustom {

    @PreAuthorize("permitAll()")
    <S extends User> S save(S s);

    @PreAuthorize("permitAll()")
    User findOne(Long aLong);

}
