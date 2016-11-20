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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Rooms.
 *
 * @see com.viktorban.wlgame.model.Room
 */
@RestController
public class RoomController implements Serializable {

    /**
     * An object used to make a room with.
     */
    private static class FutureRoom {

        /**
         * The ID of the From language.
         */
        private String languageFrom;

        /**
         * The ID of the To language.
         */
        private String languageTo;

        /**
         * Maximum number of players.
         */
        private int maxPlayers;

        /**
         * Returns the ID of the From language.
         *
         * @return The ID of the From language.
         */
        public String getLanguageFrom() {
            return languageFrom;
        }

        /**
         * Sets the ID of the From language.
         *
         * @param languageFrom The ID of the From language.
         */
        public void setLanguageFrom(String languageFrom) {
            this.languageFrom = languageFrom;
        }

        /**
         * Returns the ID of the To language.
         *
         * @return The ID of the To language.
         */
        public String getLanguageTo() {
            return languageTo;
        }

        /**
         * Sets the ID of the To language.
         *
         * @param languageTo The ID of the To language.
         */
        public void setLanguageTo(String languageTo) {
            this.languageTo = languageTo;
        }

        /**
         * Returns the maximum number of players.
         *
         * @return The maximum number of players.
         */
        public int getMaxPlayers() {
            return maxPlayers;
        }

        /**
         * Sets the maximum number of players.
         *
         * @param maxPlayers The maximum number of players.
         */
        public void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

    }

    /**
     * Logger object.
     */
    private static Log log = LogFactory.getLog(RoomController.class);

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the open game rooms.
     *
     * @return The open game rooms.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms")
    @PreAuthorize("hasAuthority('PLAYER')")
    public HttpEntity<?> getRooms() {
        return new ResponseEntity<>(new Resources(
                entityManager.createQuery("SELECT r FROM com.viktorban.wlgame.model.Room r WHERE r.state <> 'ENDED'").getResultList(),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRooms()).withRel("self"),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRooms()).withRel("rooms")
        ), HttpStatus.OK);
    }

    /**
     * Opens a new room.
     *
     * @param futureRoom Data to open the room with.
     * @return The opened room.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms")
    public HttpEntity<?> openRoom(@RequestBody FutureRoom futureRoom) {
        User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
        if (player.getActiveRoomId() != null) {
            throw new AccessDeniedException("Already part of an ongoing room");
        }

        // Check max player count.
        if (futureRoom.getMaxPlayers() < 2 || futureRoom.getMaxPlayers() > 5) {
            throw new BadRequestException("Invalid maxPlayer value - should be between 2 and 5 (incl.)");
        }

        // Load languages.
        Language langLanguageFrom;
        Language langLanguageTo;
        try {
            langLanguageFrom = entityManager.find(Language.class, futureRoom.getLanguageFrom());
            langLanguageTo = entityManager.find(Language.class, futureRoom.getLanguageTo());
        } catch (NoResultException e) {
            throw new BadRequestException("Invalid languageFrom or languageTo value");
        }

        // Create room and return it.
        Room room = new Room(futureRoom.getMaxPlayers(), langLanguageFrom, langLanguageTo);
        RoomPlayer roomPlayer = room.join(player);
        entityManager.persist(room);
        entityManager.persist(roomPlayer);
        log.info("Opened " + room.toString());
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    /**
     * Returns the details of a room.
     *
     * @param id The room's ID.
     * @return The room's details.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms/{id}")
    public HttpEntity<?> getRoom(@PathVariable("id") String id) {
        try {
            Long longId = Long.parseLong(id);
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

    /**
     * Makes the logged in user join a room.
     *
     * @param id The room's ID.
     * @return The room's details.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/join")
    public HttpEntity<?> joinRoom(@PathVariable("id") String id) {
        try {
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            RoomPlayer roomPlayer = room.join(player);
            entityManager.persist(roomPlayer);
            entityManager.flush();
            log.info("Player " + player.getName() + " joined " + room.toString());
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Uploads words into a room.
     *
     * @param id The room's ID.
     * @param uploadedWords The words to upload.
     * @return The room's details.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/upload_words")
    public HttpEntity<?> uploadWords(@PathVariable("id") String id, @RequestBody Map<String, String> uploadedWords) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            List<Word> wordList = new ArrayList<>();

            // Make the word objects.
            for (Map.Entry<String, String> uploadedWord : uploadedWords.entrySet()) {
                // Get or create the primary language word.
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

                // Get or create the translation.
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

                // Add the translation to the list of word's translations if not already in there.
                if (!word.getTranslations().contains(translation)) {
                    word.addTranslation(translation);
                }
                wordList.add(word);
            }

            // Save to database.
            room.uploadWords(player, wordList);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Moves the player to the MEMORIZING state.
     *
     * @param id The room's ID.
     * @return The room's details.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/start_memorizing")
    public HttpEntity<?> startMemorizing(@PathVariable("id") String id) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            room.startMemorizing(player);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Returns the list of uploaded words.
     *
     * @param id The room's ID.
     * @return The list of uploaded words.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms/{id}/words")
    public HttpEntity<?> getWords(@PathVariable("id") String id) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            if (room.getState() == Room.RoomState.IN_PROGRESS || room.getState() == Room.RoomState.ENDED) {
                return new ResponseEntity<>(room.getWords(), HttpStatus.OK);
            }
            throw new AccessDeniedException("Words can only be requested when the game is in progress.");
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Returns the list of translations for each word in a map.
     *
     * @param id The room's ID.
     * @return The translation map.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms/{id}/translations")
    public HttpEntity<?> getTranslations(@PathVariable("id") String id) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            RoomPlayer roomPlayer = room.getRoomPlayer(player);
            if (roomPlayer == null || roomPlayer.getState() == RoomPlayer.RoomPlayerState.MEMORIZING) {
                return new ResponseEntity<>(room.getTranslations(), HttpStatus.OK);
            }
            throw new AccessDeniedException("Translations can only be requested during memorizing.");
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Moves the player to the SOLVING state.
     *
     * @param id The room's ID.
     * @return The room's details.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/start_solving")
    public HttpEntity<?> startSolving(@PathVariable("id") String id) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            room.startSolving(player);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Uploads solutions into a room.
     *
     * @param id The room's ID.
     * @param uploadedSolutions The solutions to upload.
     * @return The room's details.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.POST, path = "/api/rooms/{id}/upload_solutions")
    public HttpEntity<?> uploadSolutions(@PathVariable("id") String id, @RequestBody Map<Long, String> uploadedSolutions) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());

            // Make solution objects.
            List<Solution> solutionList = new ArrayList<>();
            for (Map.Entry<Long, String> uploadedSolution : uploadedSolutions.entrySet()) {
                Word word = entityManager.find(Word.class, uploadedSolution.getKey());
                if (word == null) {
                    throw new BadRequestException("Invalid uploaded solutions.");
                }
                Solution solution = new Solution(room, player, word, uploadedSolution.getValue());
                entityManager.persist(solution);
                solutionList.add(solution);
            }

            // Save to database.
            room.uploadSolutions(player, solutionList);
            entityManager.flush();
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Returns the list of solutions the player uploaded into the room.
     *
     * @param id The room's ID.
     * @return The uploaded solutions.
     */
    @Transactional
    @PreAuthorize("hasAuthority('PLAYER')")
    @RequestMapping(method = RequestMethod.GET, path = "/api/rooms/{id}/solutions")
    public HttpEntity<?> getSolutions(@PathVariable("id") String id) {
        try {
            // Get the room and the player.
            Long longId = Long.parseLong(id);
            Room room = entityManager.find(Room.class, longId);
            if (room == null) {
                throw new NoResultException();
            }
            User player = entityManager.find(User.class, Application.getCurrentUser().getUserId());
            RoomPlayer roomPlayer = room.getRoomPlayer(player);
            if (roomPlayer.getState() == RoomPlayer.RoomPlayerState.DONE) {
                return new ResponseEntity<>(room.getSolutions(player), HttpStatus.OK);
            }
            throw new AccessDeniedException("Solutions can only be requested after they are uploaded.");
        } catch (NumberFormatException | NoResultException e) {
            throw new ResourceNotFoundException();
        }
    }

}
