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

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a game room entity.
 */
@Entity
@Table(name = "rooms")
public class Room extends ResourceSupport {

    public static final int wordsPerPlayer = 10;
    public static final long timeoutJoin = 5 * 60 * 1000L;
    public static final long timeoutUploadWords = 5 * 60 * 1000L;
    public static final long timeoutUploadSolutions = 10 * 60 * 1000L;

    /**
     * Enumeration to represent different room states.
     */
    public enum RoomState {
        WAITING_FOR_PLAYERS, WAITING_FOR_WORDS, IN_PROGRESS, ENDED
    }

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
   private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private RoomState state;

    @Column(name = "max_players")
    private int maxPlayers;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language1_id", referencedColumnName = "id")
    private Language languageFrom;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language2_id", referencedColumnName = "id")
    private Language languageTo;

    @Column(name = "timed_out")
    private boolean timedOut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opened")
    private Date opened;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined")
    private Date joined;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started")
    private Date started;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ended")
    private Date ended;

    @OneToMany(mappedBy = "room")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RoomPlayer> roomPlayers;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "rooms_words",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id")
    )
    private List<Word> words;

    @OneToMany(mappedBy = "room")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Solution> solutions;

    public Room() {
        roomPlayers = new ArrayList<>();
        words = new ArrayList<>();
        solutions = new ArrayList<>();
        this.setState(RoomState.WAITING_FOR_PLAYERS);
        this.setTimedOut(false);
    }

    public Room(int maxPlayers, Language languageFrom, Language languageTo) {
        this();
        this.setMaxPlayers(maxPlayers);
        this.setLanguageFrom(languageFrom);
        this.setLanguageTo(languageTo);
    }

    @JsonIgnore
    public List<User> getPlayers() {
        return roomPlayers.stream().map(RoomPlayer::getPlayer).collect(Collectors.toList());
    }

    public RoomPlayer getRoomPlayer(User player) {
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (roomPlayer.getPlayer().equals(player)) {
                return roomPlayer;
            }
        }
        return null;
    }

    public synchronized RoomPlayer join(User player) throws InvalidRoomActionException, RoomFullException, AlreadyJoinedException {
        // Throw an exception if there's a problem.
        if (state != RoomState.WAITING_FOR_PLAYERS) {
            throw new InvalidRoomActionException();
        } else if (roomPlayers.size() >= maxPlayers) {
            throw new RoomFullException();
        } else if (getPlayers().contains(player)) {
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

    public synchronized void uploadWords(User player, Collection<Word> words) throws InvalidRoomActionException, InvalidNumberOfWordsException, AlreadyUploadedWordsException {
        // Throw an exception if there's a problem.
        if (state != RoomState.WAITING_FOR_WORDS) {
            throw new InvalidRoomActionException();
        } else if (words.size() != wordsPerPlayer) {
            throw new InvalidNumberOfWordsException();
        } else if (getRoomPlayer(player).getUploadedWords()) {
            throw new AlreadyUploadedWordsException();
        }

        // Add the new words.
        words.stream().filter(word -> !this.words.contains(word)).forEach(this.words::add);
        getRoomPlayer(player).setUploadedWords(true);

        // Determine if the room can move to the next state.
        boolean done = true;
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (!roomPlayer.getUploadedWords()) {
                done = false;
                break;
            }
        }
        if (done) {
            this.setState(RoomState.IN_PROGRESS);
        }
    }

    public synchronized void uploadSolutions(User player, Collection<Solution> solutions) throws InvalidRoomActionException, InvalidNumberOfSolutionsException, AlreadyUploadedSolutionsException {
        // Throw an exception if there's a problem.
        if (state != RoomState.IN_PROGRESS) {
            throw new InvalidRoomActionException();
        } else if (solutions.size() != words.size()) {
            throw new InvalidNumberOfSolutionsException();
        } else if (getRoomPlayer(player).getUploadedSolutions()) {
            throw new AlreadyUploadedSolutionsException();
        }

        // Add solutions.
        this.solutions.addAll(solutions);
        getRoomPlayer(player).setUploadedSolutions(true);

        // Determine if the room can move to the next state.
        boolean done = true;
        for (RoomPlayer roomPlayer : roomPlayers) {
            if (!roomPlayer.getUploadedSolutions()) {
                done = false;
                break;
            }
        }
        if (done) {
            this.setState(RoomState.ENDED);
        }
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        links.addAll(super.getLinks());

        String stringId = Long.toString(id);
        links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRoom(stringId)).withRel("self"));
        links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).getRoom(stringId)).withRel("room"));

        User player = Application.getCurrentUser();

        switch (state) {
            case WAITING_FOR_PLAYERS:
                if (!getPlayers().contains(player)) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).joinRoom(stringId)).withRel("join"));
                }
                break;

            case WAITING_FOR_WORDS:
                if (getRoomPlayer(player) != null && !getRoomPlayer(player).getUploadedWords()) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).uploadWords(stringId, null)).withRel("upload_words"));
                }
                break;

            case IN_PROGRESS:
                if (getRoomPlayer(player) != null && !getRoomPlayer(player).getUploadedSolutions()) {
                    links.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(RoomController.class).uploadSolutions(stringId, null)).withRel("upload_solutions"));
                }
                break;
        }
        return links;
    }

    @JsonProperty("id")
    public Long getRoomId() {
        return id;
    }

    public RoomState getState() {
        return state;
    }

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
                break;

            case ENDED:
                this.setEnded(new Date());
                break;
        }
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Language getLanguageFrom() {
        return languageFrom;
    }

    public void setLanguageFrom(Language languageFrom) {
        this.languageFrom = languageFrom;
    }

    public Language getLanguageTo() {
        return languageTo;
    }

    public void setLanguageTo(Language languageTo) {
        this.languageTo = languageTo;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    public Date getJoined() {
        return joined;
    }

    public void setJoined(Date joined) {
        this.joined = joined;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public List<String> getWinners() {
        if (timedOut || state != RoomState.ENDED) {
            return new ArrayList<>();
        }

        // Initialize a score map.
        Map<User, Integer> scores = new HashMap<>();
        for (User player : getPlayers()) {
            scores.put(player, 0);
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

    public List<RoomPlayer> getRoomPlayers() {
        return roomPlayers;
    }

    public void setRoomPlayers(List<RoomPlayer> roomPlayers) {
        this.roomPlayers = roomPlayers;
    }

    public List<Word> getWords() {
        if (state == RoomState.IN_PROGRESS || state == RoomState.ENDED) {
            return words;
        }
        return null;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Solution> getSolutions() {
        if (state == RoomState.ENDED) {
            // Return the current user's own solutions.
            return solutions.stream().filter(solution -> solution.getPlayer().equals(Application.getCurrentUser())).collect(Collectors.toList());
        }
        return null;
    }

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
