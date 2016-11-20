package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktorban.wlgame.Application;
import com.viktorban.wlgame.controller.RoomController;
import com.viktorban.wlgame.exception.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a game room entity.
 */
@Entity
@Table(name = "rooms")
public class Room extends ResourceSupport {

    /**
     * Enumeration to represent different room states.
     */
    public enum RoomState {
        WAITING_FOR_PLAYERS, WAITING_FOR_WORDS, IN_PROGRESS, ENDED
    }

    /**
     * Specifies how many words each player has to upload.
     */
    public static final int wordsPerPlayer = 10;

    /**
     * Specifies the amount of time to wait for players to join before closing the room.
     */
    public static final long timeoutJoin = 5 * 60 * 1000L;

    /**
     * Specifies the amount of time to wait for players to upload words before closing the room.
     */
    public static final long timeoutUploadWords = 5 * 60 * 1000L;

    /**
     * Specifies the amount of time to wait for players to upload their solutions.
     */
    public static final long timeoutUploadSolutions = 35 * 60 * 1000L;

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    /**
     * The room's state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private RoomState state;

    /**
     * The maximum number of players allowed to join.
     */
    @Column(name = "max_players")
    private int maxPlayers;

    /**
     * The primary language.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language1_id", referencedColumnName = "id")
    private Language languageFrom;

    /**
     * The secondary language.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language2_id", referencedColumnName = "id")
    private Language languageTo;

    /**
     * Whether the room has timed out.
     */
    @Column(name = "timed_out")
    private boolean timedOut;

    /**
     * When the room was opened.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opened")
    private Date opened;

    /**
     * When all the players joined.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined")
    private Date joined;

    /**
     * When all the players uploaded their word lists.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started")
    private Date started;

    /**
     * When the game ended.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ended")
    private Date ended;

    /**
     * List of players inside the room.
     */
    @OneToMany(mappedBy = "room")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RoomPlayer> roomPlayers;

    /**
     * The uploaded words.
     */
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "rooms_words",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id")
    )
    private List<Word> words;

    /**
     * The uploaded solutions.
     */
    @OneToMany(mappedBy = "room")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Solution> solutions;

    /**
     * Room default constructor.
     */
    public Room() {
        roomPlayers = new ArrayList<>();
        words = new ArrayList<>();
        solutions = new ArrayList<>();
        this.setState(RoomState.WAITING_FOR_PLAYERS);
        this.setTimedOut(false);
    }

    /**
     * Room constructor specifying all the required fields.
     *
     * @param maxPlayers   The maximum number of players.
     * @param languageFrom The primary language.
     * @param languageTo   The secondary language.
     */
    public Room(int maxPlayers, Language languageFrom, Language languageTo) {
        this();
        this.setMaxPlayers(maxPlayers);
        this.setLanguageFrom(languageFrom);
        this.setLanguageTo(languageTo);
    }

    /**
     * Returns the RoomPlayer object for the given player.
     *
     * @param player The player.
     * @return The player's RoomPlayer object or null if not part of the room.
     */
    public RoomPlayer getRoomPlayer(User player) {
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (roomPlayer.getPlayer().equals(player)) {
                return roomPlayer;
            }
        }
        return null;
    }

    /**
     * Joins the given player into the room.
     *
     * @param player The player to join.
     * @return The created RoomPlayer object.
     *
     * @throws InvalidRoomActionException If not acceptable in the current room state.
     * @throws RoomFullException          If the room is full.
     * @throws AlreadyJoinedException     If the player has already joined.
     */
    public synchronized RoomPlayer join(User player) throws InvalidRoomActionException, RoomFullException, AlreadyJoinedException {
        // Throw an exception if there's a problem.
        if (state != RoomState.WAITING_FOR_PLAYERS) {
            throw new InvalidRoomActionException();
        } else if (roomPlayers.size() >= maxPlayers) {
            throw new RoomFullException();
        } else if (getRoomPlayer(player) != null) {
            throw new AlreadyJoinedException();
        }

        // Add the player and some defaults.
        RoomPlayer roomPlayer = new RoomPlayer(this, player);
        roomPlayers.add(roomPlayer);

        // Determine if the room can move to the next state.
        if (roomPlayers.size() == maxPlayers) {
            this.setState(RoomState.WAITING_FOR_WORDS);
        }

        return roomPlayer;
    }

    /**
     * Uploads words into the room.
     *
     * @param player The player.
     * @param words  The player's words.
     *
     * @throws InvalidRoomActionException    If not acceptable in the current room state.
     * @throws InvalidNumberOfWordsException If the number of words doesn't match the required number.
     * @throws AlreadyUploadedWordsException If the player has already uploaded words.
     */
    public synchronized void uploadWords(User player, Collection<Word> words) throws InvalidRoomActionException, InvalidNumberOfWordsException, AlreadyUploadedWordsException {
        // Throw an exception if there's a problem.
        if (state != RoomState.WAITING_FOR_WORDS) {
            throw new InvalidRoomActionException();
        } else if (words.size() != wordsPerPlayer) {
            throw new InvalidNumberOfWordsException();
        } else if (getRoomPlayer(player).getState() == RoomPlayer.RoomPlayerState.WAITING_FOR_ROOM) {
            throw new AlreadyUploadedWordsException();
        }

        // Add the new words.
        words.stream().filter(word -> !this.words.contains(word)).forEach(this.words::add);
        getRoomPlayer(player).setState(RoomPlayer.RoomPlayerState.WAITING_FOR_ROOM);

        // Determine if the room can move to the next state.
        boolean done = true;
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (roomPlayer.getState() != RoomPlayer.RoomPlayerState.WAITING_FOR_ROOM) {
                done = false;
                break;
            }
        }
        if (done) {
            this.setState(RoomState.IN_PROGRESS);
        }
    }

    /**
     * Moves the player into MEMORIZING state.
     *
     * @param player The player.
     * @throws InvalidPlayerStateChangeException If the player is not in READY state.
     * @throws PlayerNotPartOfRoomException      If the player is not part of the room.
     */
    public synchronized void startMemorizing(User player) throws InvalidPlayerStateChangeException, PlayerNotPartOfRoomException {
        RoomPlayer roomPlayer = getRoomPlayer(player);
        if (roomPlayer != null) {
            if (roomPlayer.getState() == RoomPlayer.RoomPlayerState.READY) {
                roomPlayer.setState(RoomPlayer.RoomPlayerState.MEMORIZING);
            } else {
                throw new InvalidPlayerStateChangeException();
            }
        } else {
            throw new PlayerNotPartOfRoomException();
        }
    }

    /**
     * Moves the player into SOLVING state.
     *
     * @param player The player.
     * @throws InvalidPlayerStateChangeException If the player is not in MEMORIZING state.
     * @throws PlayerNotPartOfRoomException      If the player is not part of the room.
     */
    public synchronized void startSolving(User player) throws InvalidPlayerStateChangeException, PlayerNotPartOfRoomException {
        RoomPlayer roomPlayer = getRoomPlayer(player);
        if (roomPlayer != null) {
            if (roomPlayer.getState() == RoomPlayer.RoomPlayerState.MEMORIZING) {
                roomPlayer.setState(RoomPlayer.RoomPlayerState.SOLVING);
            } else {
                throw new InvalidPlayerStateChangeException();
            }
        } else {
            throw new PlayerNotPartOfRoomException();
        }
    }

    /**
     * Uploads solutions into the room.
     *
     * @param player    The player.
     * @param solutions The player's solutions.
     * @throws InvalidRoomActionException        If not acceptable in the current room state.
     * @throws InvalidNumberOfSolutionsException If the number of solutions doesn't match the required number.
     * @throws AlreadyUploadedSolutionsException If the player has already uploaded solutions.
     */
    public synchronized void uploadSolutions(User player, Collection<Solution> solutions) throws InvalidRoomActionException, InvalidNumberOfSolutionsException, AlreadyUploadedSolutionsException {
        // Throw an exception if there's a problem.
        if (state != RoomState.IN_PROGRESS) {
            throw new InvalidRoomActionException();
        } else if (solutions.size() != words.size()) {
            throw new InvalidNumberOfSolutionsException();
        } else if (getRoomPlayer(player).getState() == RoomPlayer.RoomPlayerState.DONE) {
            throw new AlreadyUploadedSolutionsException();
        }

        // Add solutions.
        this.solutions.addAll(solutions);
        getRoomPlayer(player).setState(RoomPlayer.RoomPlayerState.DONE);

        // Determine if the room can move to the next state.
        boolean done = true;
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (roomPlayer.getState() != RoomPlayer.RoomPlayerState.DONE) {
                done = false;
                break;
            }
        }
        if (done) {
            this.setState(RoomState.ENDED);
        }
    }

    /**
     * Returns when the room will automatically time out or end.
     *
     * @return When the room will automatically time out or end.
     */
    public Date getEndsAt() {
        switch (state) {
            case WAITING_FOR_PLAYERS:
                return new Date(opened.getTime() + timeoutJoin);

            case WAITING_FOR_WORDS:
                return new Date(joined.getTime() + timeoutUploadWords);

            case IN_PROGRESS:
                return new Date(started.getTime() + timeoutUploadSolutions);
        }
        return null;
    }

    /**
     * Returns the list of winners' names.
     *
     * @return The list of winners' names.
     */
    public List<String> getWinners() {
        if (timedOut || state != RoomState.ENDED) {
            return new ArrayList<>();
        }

        // Initialize a score map.
        Map<User, Integer> scores = new HashMap<>();
        for (RoomPlayer roomPlayer : roomPlayers) {
            scores.put(roomPlayer.getPlayer(), 0);
        }

        // Evaluate uploaded solutions and determine scores.
        solutions.stream().filter(Solution::isCorrect).forEach(solution -> scores.put(solution.getPlayer(), scores.get(solution.getPlayer()) + 1));

        // Select winner.
        int highScore = -1;
        List<String> winners = new ArrayList<>();
        for (Map.Entry<User, Integer> scoreEntry : scores.entrySet()) {
            if (scoreEntry.getValue() < highScore) {
                continue;
            }
            if (scoreEntry.getValue() > highScore) {
                winners.clear();
                highScore = scoreEntry.getValue();
            }
            winners.add(scoreEntry.getKey().getName());
        }
        return winners;
    }

    /**
     * Returns the list of uploaded solutions of a given player.
     *
     * @param player The player.
     * @return The list of uplaoded solutions.
     */
    @JsonIgnore
    public List<Solution> getSolutions(User player) {
        return solutions.stream().filter(solution -> solution.getPlayer().equals(player)).collect(Collectors.toList());
    }

    /**
     * Returns a map of word IDs and their translations.
     *
     * @return A map of word IDs and their translations.
     */
    @JsonIgnore
    public Map<Long, List<String>> getTranslations() {
        Map<Long, List<String>> translations = new HashMap<>();
        for (Word word : words) {
            translations.put(word.getId(), word.getTranslationStrings(languageTo));
        }
        return translations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        links.addAll(super.getLinks());

        String stringId = Long.toString(id);
        links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRoom(stringId)).withRel("self"));
        links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRoom(stringId)).withRel("room"));

        User player = Application.getCurrentUser();
        RoomPlayer roomPlayer = getRoomPlayer(player);

        switch (state) {
            case WAITING_FOR_PLAYERS:
                if (roomPlayer == null) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).joinRoom(stringId)).withRel("join"));
                }
                break;

            case WAITING_FOR_WORDS:
                if (roomPlayer != null && getRoomPlayer(player).getState() == RoomPlayer.RoomPlayerState.JOINED) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).uploadWords(stringId, null)).withRel("upload_words"));
                }
                break;

            case IN_PROGRESS:
                links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getWords(stringId)).withRel("words"));
                if (roomPlayer != null) {
                    switch (roomPlayer.getState()) {
                        case READY:
                            links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).startMemorizing(stringId)).withRel("start_memorizing"));
                            break;

                        case MEMORIZING:
                            links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getTranslations(stringId)).withRel("translations"));
                            links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).startSolving(stringId)).withRel("start_solving"));
                            break;

                        case SOLVING:
                            links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).uploadSolutions(stringId, null)).withRel("upload_solutions"));
                            break;
                    }
                }
                break;

            case ENDED:
                if (roomPlayer != null) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getSolutions(stringId)).withRel("solutions"));
                }
        }
        return links;
    }

    /**
     * Returns the room's ID.
     *
     * @return The room's ID.
     */
    @JsonProperty("id")
    public Long getRoomId() {
        return id;
    }

    /**
     * Returns the room's state.
     *
     * @return The room's state.
     */
    public RoomState getState() {
        return state;
    }

    /**
     * Sets the room's state.
     *
     * @param state The room's new state.
     */
    public synchronized void setState(RoomState state) {
        this.state = state;
        switch (state) {
            case WAITING_FOR_PLAYERS:
                this.setOpened(new Date());
                break;

            case WAITING_FOR_WORDS:
                this.setJoined(new Date());
                break;

            case IN_PROGRESS:
                this.setStarted(new Date());
                for (RoomPlayer roomPlayer : roomPlayers) {
                    roomPlayer.setState(RoomPlayer.RoomPlayerState.READY);
                }
                break;

            case ENDED:
                this.setEnded(new Date());
                break;
        }
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

    /**
     * Returns the primary language.
     *
     * @return The primary lanugage.
     */
    public Language getLanguageFrom() {
        return languageFrom;
    }

    /**
     * Sets the primary language.
     *
     * @param languageFrom The primary language.
     */
    public void setLanguageFrom(Language languageFrom) {
        this.languageFrom = languageFrom;
    }

    /**
     * Return the secondary language.
     *
     * @return The secondary language.
     */
    public Language getLanguageTo() {
        return languageTo;
    }

    /**
     * Sets the secondary language.
     *
     * @param languageTo The secondary lanugage.
     */
    public void setLanguageTo(Language languageTo) {
        this.languageTo = languageTo;
    }

    /**
     * Returns whether the room has timed out.
     *
     * @return Whether the room has timed out.
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    /**
     * Sets whether the room has timed out.
     *
     * @param timedOut Whether the room has timed out.
     */
    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    /**
     * Returns when the room was opened.
     *
     * @return When the room was opened.
     */
    public Date getOpened() {
        return opened;
    }

    /**
     * Sets when the room was opened.
     *
     * @param opened When the room was opened.
     */
    public void setOpened(Date opened) {
        this.opened = opened;
    }

    /**
     * Returns when everyone joined the room.
     *
     * @return When everyone joined the room.
     */
    public Date getJoined() {
        return joined;
    }

    /**
     * Sets when everyone joined the room.
     *
     * @param joined When everyone joined the room.
     */
    public void setJoined(Date joined) {
        this.joined = joined;
    }

    /**
     * Returns when everyone uploaded their solutions.
     *
     * @return When everyone uploaded their solutions.
     */
    public Date getStarted() {
        return started;
    }

    /**
     * Sets when everyone uploaded their solutions.
     *
     * @param started When everyone uploaded their solutions.
     */
    public void setStarted(Date started) {
        this.started = started;
    }

    /**
     * Returns when the game ended.
     *
     * @return When the game ended.
     */
    public Date getEnded() {
        return ended;
    }

    /**
     * Sets when the game ended.
     *
     * @param ended When the game ended.
     */
    public void setEnded(Date ended) {
        this.ended = ended;
    }

    /**
     * Returns the list of Room-Player associations.
     *
     * @return The list of Room-Player associations.
     */
    public List<RoomPlayer> getRoomPlayers() {
        return roomPlayers;
    }

    /**
     * Sets the list of Room-Player associations.
     *
     * @param roomPlayers The list of Room-Player associations.
     */
    public void setRoomPlayers(List<RoomPlayer> roomPlayers) {
        this.roomPlayers = roomPlayers;
    }

    /**
     * Returns the list of uploaded words.
     *
     * @return The list of uplaoded words.
     */
    @JsonIgnore
    public List<Word> getWords() {
        return words;
    }

    /**
     * Sets the list of uploaded words.
     *
     * @param words The list of uploaded words.
     */
    public void setWords(List<Word> words) {
        this.words = words;
    }

    /**
     * Returns the list of uplaoded solutions.
     *
     * @return The list of uplaoded solutions.
     */
    public List<Solution> getSolutions() {
        return solutions;
    }

    /**
     * Sets the list of uploaded solutions.
     *
     * @param solutions The list of uploaded solutions.
     */
    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Room (id: " + getRoomId() + ", maxPlayers: " + getMaxPlayers() + ", lang: " + getLanguageFrom().getId() + "-" + getLanguageTo().getId() +")";
    }

}
