package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "solutions")
public class Solution {

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
   private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private User player;

    @OneToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;

    @Column(name = "input")
    private String input;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="room_id")
    private Room room;

    @Column(name = "correct")
    private boolean correct;

    public Solution() {}

    public Solution(Room room, User player, Word word, String input) {
        this.setRoom(room);
        this.setPlayer(player);
        this.setWord(word);
        this.setInput(input);
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input.trim().toLowerCase();
        correct = false;
        for (Word translation : word.getTranslations(room.getLanguageTo())) {
            if (translation.getWord().toLowerCase().equals(input)) {
                correct = true;
                break;
            }
        }
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @JsonIgnore
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getExpected() {
        List<String> translations = word.getTranslations(room.getLanguageTo()).stream().map(Word::getWord).collect(Collectors.toList());
        return String.join("; ", translations);
    }

}
