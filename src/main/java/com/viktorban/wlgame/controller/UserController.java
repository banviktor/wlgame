package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.model.User;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * REST controller for Users.
 *
 * @see com.viktorban.wlgame.model.User
 */
@BasePathAwareController
public class UserController {

    /**
     * Returns the logged in user.
     *
     * @return The logged in user.
     */
    @RequestMapping(path = "/self", method = RequestMethod.GET)
    public HttpEntity<User> currentUser() {
        User user = Application.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
