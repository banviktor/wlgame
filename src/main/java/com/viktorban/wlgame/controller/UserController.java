package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.model.User;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * REST controller for Users.
 *
 * @see com.viktorban.wlgame.model.User
 */
@BasePathAwareController
public class UserController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the logged in user.
     *
     * @return The logged in user.
     */
    @RequestMapping(path = "/self", method = RequestMethod.GET)
    public HttpEntity<?> currentUser() {
        User user = entityManager.find(User.class, Application.getCurrentUser().getUserId());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
