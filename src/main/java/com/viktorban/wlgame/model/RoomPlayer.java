package com.viktorban.wlgame.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity for additional columns in the Room-Player join table.
 */
@Entity
@IdClass(RoomPlayerId.class)
public class RoomPlayer {

    /**
     * Enumeration to represent different player states.
     */
    public enum RoomPlayerState {
        JOINED, WAITING_FOR_ROOM, READY, MEMORIZING, SOLVING, DONE, TIMED_OUT
    }

    /**
     * The time each player can spend on memorizing the words.
     */
    public static final long timeoutMemorize = 10 * 60 * 1000L;

    /**
     * The room in the association.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    /**
     * The player in the association.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User player;

    /**
     * The player's state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private RoomPlayerState state;

    /**
     * The time when the player started memorizing the words.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_memorizing")
    private Date startedMemorizing;

    /**
     * RoomPlayer default constructor.
     */
    public RoomPlayer() {
        state = RoomPlayerState.JOINED;
    }

    /**
     * RoomPlayer constructor specifying all the required fields.
     *
     * @param room   The room.
     * @param player The player
     */
    public RoomPlayer(Room room, User player) {
        this();
        this.room = room;
        this.player = player;
    }

    /**
     * Returns the room.
     *
     * @return The room.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room.
     *
     * @param room The room to set.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Returns the player.
     *
     * @return The player.
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Returns the player's name.
     *
     * @return The player's name.
     */
    public String getPlayerName() {
        return player.getName();
    }

    /**
     * Sets the player.
     *
     * @param player The player to set.
     */
    public void setPlayer(User player) {
        this.player = player;
    }

    /**
     * Returns the player's state.
     *
     * @return The player's state.
     */
    public RoomPlayerState getState() {
        return state;
    }

    /**
     * Sets the player's state.
     *
     * @param state The player's state.
     */
    public void setState(RoomPlayerState state) {
        if (state == RoomPlayerState.MEMORIZING) {
            startedMemorizing = new Date();
        }
        this.state = state;
    }

    /**
     * Returns the time when the player started memorizing the words.
     *
     * @return The time when the player started memorizing the words.
     */
    public Date getStartedMemorizing() {
        return startedMemorizing;
    }

    /**
     * Sets the time when the player started memorizing the words.
     *
     * @param startedMemorizing The time when the player started memorizing the words.
     */
    public void setStartedMemorizing(Date startedMemorizing) {
        this.startedMemorizing = startedMemorizing;
    }

}
