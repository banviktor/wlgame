package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.UserWrapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping(path = "/api/me", method = RequestMethod.GET)
    public User currentUser() {
        UserWrapper userWrapper = (UserWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userWrapper.getUser();
    }
}
