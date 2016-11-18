package com.viktorban.wlgame.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
public class LanguageController {

    /**
     * Logger object.
     */
    private static Log log = LogFactory.getLog(RoomController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(method = RequestMethod.GET, path = "/api/languages")
    public HttpEntity<?> getLanguages() {
        return new ResponseEntity<>(entityManager.createQuery("SELECT l FROM com.viktorban.wlgame.model.Language l").getResultList(), HttpStatus.OK);
    }

}
