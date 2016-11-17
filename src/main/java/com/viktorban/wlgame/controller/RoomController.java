package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.exception.BadRequestException;
import com.viktorban.wlgame.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Rooms.
 *
 * @see com.viktorban.wlgame.model.Room
 */
@RestController
public class RoomController {

    /**
     * Logger object.
     */
    private static Log log = LogFactory.getLog(RoomController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms")
    public HttpEntity<?> getRooms() {
        return new ResponseEntity<>(new Resources(
                entityManager.createQuery("SELECT r FROM com.viktorban.wlgame.model.Room r").getResultList(),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRooms()).withRel("self"),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRooms()).withRel("rooms")
        ), HttpStatus.OK);
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms")
    public HttpEntity<?> openRoom(@RequestParam("maxPlayers") int maxPlayers,
                                  @RequestParam("languageFrom") String languageFrom,
                                  @RequestParam("languageTo") String languageTo) {
        User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
        if (player.getActiveRoomId() != null) {
            throw new AccessDeniedException("Already part of an ongoing room");
        }

        // Check max player count.
        if (maxPlayers < 2 || maxPlayers > 5) {
            throw new BadRequestException("Invalid maxPlayer value - should be between 2 and 5 (incl.)");
        }

        // Load languages.
        Language langLanguageFrom;
        Language langLanguageTo;
        try {
            langLanguageFrom = entityManager.find(Language.class, languageFrom);
            langLanguageTo = entityManager.find(Language.class, languageTo);
        } catch (NoResultException e) {
            throw new BadRequestException("Invalid languageFrom or languageTo value");
        }

        // Create room and return it.
        Room room = new Room(maxPlayers, langLanguageFrom, langLanguageTo);
        RoomPlayer roomPlayer = room.join(player);
        entityManager.persist(room);
        entityManager.persist(roomPlayer);
        log.info("Room " + room.getRoomId() + " opened.");
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms/{id}")
    public HttpEntity<?> getRoom(@PathVariable("id") String id) {
        try {
            long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/join")
    public HttpEntity<?> joinRoom(@PathVariable("id") String id) {
        try {
            long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            RoomPlayer roomPlayer = room.join(player);
            entityManager.persist(roomPlayer);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/upload_words")
    public HttpEntity<?> uploadWords(@PathVariable("id") String id, @RequestBody Map<String, String> uploadedWords) {
        try {
            long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            List<Word> wordList = new ArrayList<>();
            for (Map.Entry<String, String> uploadedWord : uploadedWords.entrySet()) {
                Word word;
                try {
                    word = (Word) entityManager
                            .createQuery("SELECT w FROM com.viktorban.wlgame.model.Word w WHERE LOWER(w.word) = :word AND w.language = :language")
                            .setParameter("language", room.getLanguageFrom())
                            .setParameter("word", uploadedWord.getKey().toLowerCase())
                            .getSingleResult();
                } catch (NoResultException e) {
                    word = new Word(room.getLanguageFrom(), uploadedWord.getKey());
                    entityManager.persist(word);
                }
                Word translation;
                try {
                    translation = (Word) entityManager
                            .createQuery("SELECT w FROM com.viktorban.wlgame.model.Word w WHERE LOWER(w.word) = :word AND w.language = :language")
                            .setParameter("language", room.getLanguageTo())
                            .setParameter("word", uploadedWord.getValue().toLowerCase())
                            .getSingleResult();
                } catch (NoResultException e) {
                    translation = new Word(room.getLanguageTo(), uploadedWord.getValue());
                    entityManager.persist(translation);
                }
                if (!word.getTranslations().contains(translation)) {
                    word.addTranslation(translation);
                }
                wordList.add(word);
            }
            room.uploadWords(player, wordList);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/upload_solutions")
    public HttpEntity<?> uploadSolutions(@PathVariable("id") String id, @RequestBody Map<String, String> uploadedSolutions) {
        try {
            long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            List<Solution> solutionList = new ArrayList<>();
            for (Map.Entry<String, String> uploadedSolution : uploadedSolutions.entrySet()) {
                Word word;
                try {
                    word = (Word) entityManager
                            .createQuery("SELECT w FROM com.viktorban.wlgame.model.Word w WHERE LOWER(w.word) = :word AND w.language = :language")
                            .setParameter("language", room.getLanguageFrom())
                            .setParameter("word", uploadedSolution.getKey().toLowerCase())
                            .getSingleResult();
                } catch (NoResultException e) {
                    throw new BadRequestException("Invalid uploaded solutions.");
                }
                Solution solution = new Solution(player, word, uploadedSolution.getValue());
                entityManager.persist(solution);
                solutionList.add(solution);
            }
            room.uploadSolutions(player, solutionList);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

}
