package com.viktorban.wlgame.model;

import java.io.Serializable;

/**
 * The composite ID class for RoomPlayer.
 *
 * @see com.viktorban.wlgame.model.RoomPlayer
 */
public class RoomPlayerId implements Serializable {

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
