package com.viktorban.wlgame.model;

import java.io.Serializable;

public class RoomPlayerId implements Serializable {

    private long room;

    private long player;

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoomPlayerId) {
            RoomPlayerId otherId = (RoomPlayerId) obj;
            return otherId.room == room && otherId.player == player;
        }
        return false;
    }

}
