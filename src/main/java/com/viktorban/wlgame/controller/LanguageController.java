package com.viktorban.wlgame.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * REST controller for Languages.
 *
 * @see com.viktorban.wlgame.model.Language
 */
@RestController
public class LanguageController {

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the languages stored in the database.
     *
     * @return The languages stored in the database.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/api/languages")
    public HttpEntity<?> getLanguages() {
        return new ResponseEntity<>(entityManager.createQuery("SELECT l FROM com.viktorban.wlgame.model.Language l").getResultList(), HttpStatus.OK);
    }

}
