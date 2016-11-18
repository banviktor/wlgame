package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.Language;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Language> languages = entityManager.createQuery("SELECT l FROM com.viktorban.wlgame.model.Language l").getResultList();
        Map<String, String> languageMap = new HashMap<>();
        for (Language language : languages) {
            languageMap.put(language.getId(), language.getName());
        }
        return new ResponseEntity<>(languageMap, HttpStatus.OK);
    }

}
