package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.UserWrapper;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Users.
 *
 * @see com.viktorban.wlgame.model.User
 */
@RestController
@BasePathAwareController
public class UserController {

    /**
     * Returns the logged in user.
     *
     * @return The logged in user.
     */
    @RequestMapping(path = "/self", method = RequestMethod.GET)
    public User currentUser() {
        UserWrapper userWrapper = (UserWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userWrapper.getUser();
    }

}
