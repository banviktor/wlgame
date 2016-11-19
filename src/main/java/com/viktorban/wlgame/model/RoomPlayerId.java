package com.viktorban.wlgame.model;

import java.io.Serializable;

public class RoomPlayerId implements Serializable {

    private Long room;

    private Long player;

    public Long getRoom() {
        return room;
    }

    public void setRoom(Long room) {
        this.room = room;
    }

    public Long getPlayer() {
        return player;
    }

    public void setPlayer(Long player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoomPlayerId) {
            RoomPlayerId otherId = (RoomPlayerId) obj;
            return otherId.room.equals(room) && otherId.player.equals(player);
        }
        return false;
    }

}
