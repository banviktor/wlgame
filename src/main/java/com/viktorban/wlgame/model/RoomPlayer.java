package com.viktorban.wlgame.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity for additional columns in the Room-Player join table.
 */
@Entity
@IdClass(RoomPlayer.RoomPlayerId.class)
public class RoomPlayer {

    /**
     * The composite ID class for RoomPlayer.
     */
    public static class RoomPlayerId implements Serializable {

        /**
         * The room's ID.
         */
        private Long room;

        /**
         * The player's ID.
         */
        private Long player;

        /**
         * Returns the room's ID.
         *
         * @return The room's ID.
         */
        public Long getRoom() {
            return room;
        }

        /**
         * Sets the room's ID.
         *
         * @param room The room's ID to set.
         */
        public void setRoom(Long room) {
            this.room = room;
        }

        /**
         * Returns the player's ID.
         *
         * @return The player's ID.
         */
        public Long getPlayer() {
            return player;
        }

        /**
         * Sets the player's ID.
         * @param player The player's ID to set.
         */
        public void setPlayer(Long player) {
            this.player = player;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RoomPlayerId) {
                RoomPlayerId otherId = (RoomPlayerId) obj;
                return otherId.room.equals(room) && otherId.player.equals(player);
            }
            return false;
        }

    }

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
     * Whether the player has uploaded words into the room.
     */
    @Column(name = "uploaded_words")
    private Boolean uploadedWords;

    /**
     * Whether the player has uploaded solutions into the room.
     */
    @Column(name = "uploaded_solutions")
    private Boolean uploadedSolutions;

    /**
     * RoomPlayer default constructor.
     */
    public RoomPlayer() {
        uploadedWords = false;
        uploadedSolutions = false;
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
     * Returns whether the player has uploaded words.
     *
     * @return Whether the player has uploaded words.
     */
    public Boolean getUploadedWords() {
        return uploadedWords;
    }

    /**
     * Sets whether the player has uploaded words.
     *
     * @param uploadedWords Whether the player has uploaded words.
     */
    public void setUploadedWords(Boolean uploadedWords) {
        this.uploadedWords = uploadedWords;
    }

    /**
     * Returns whether the player has uploaded solutions.
     *
     * @return Whether the player has uploaded solutions.
     */
    public Boolean getUploadedSolutions() {
        return uploadedSolutions;
    }

    /**
     * Sets whether the player has uploaded solutions.
     *
     * @param uploadedSolutions Whether the player has uploaded solutions.
     */
    public void setUploadedSolutions(Boolean uploadedSolutions) {
        this.uploadedSolutions = uploadedSolutions;
    }

}
