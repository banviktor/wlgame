package com.viktorban.wlgame.model;

import javax.persistence.*;

@Entity
@IdClass(RoomPlayerId.class)
public class RoomPlayer {

    @Id
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User player;

    @Column(name = "uploaded_words")
    private Boolean uploadedWords;

    @Column(name = "uploaded_solutions")
    private Boolean uploadedSolutions;

    public RoomPlayer() {
        uploadedWords = false;
        uploadedSolutions = false;
    }

    public RoomPlayer(Room room, User player) {
        this();
        this.room = room;
        this.player = player;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return player.getName();
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Boolean getUploadedWords() {
        return uploadedWords;
    }

    public void setUploadedWords(Boolean uploadedWords) {
        this.uploadedWords = uploadedWords;
    }

    public Boolean getUploadedSolutions() {
        return uploadedSolutions;
    }

    public void setUploadedSolutions(Boolean uploadedSolutions) {
        this.uploadedSolutions = uploadedSolutions;
    }

}
