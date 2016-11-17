package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.model.User;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * REST controller for Users.
 *
 * @see com.viktorban.wlgame.model.User
 */
@RestController
public class UserController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the logged in user.
     *
     * @return The logged in user.
     */
    @RequestMapping(path = "/api/self", method = RequestMethod.GET)
    public HttpEntity<?> currentUser() {
        User user = Application.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user = entityManager.find(User.class, user.getUserId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
