package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents uploaded solutions.
 */
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

    /**
     * The player that uploaded the solution.
     */
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private User player;

    /**
     * The word this is a solution to.
     */
    @OneToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;

    /**
     * The user's input.
     */
    @Column(name = "input")
    private String input;

    /**
     * The room this solution was uploaded to.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="room_id")
    private Room room;

    /**
     * Whether the solution is considered correct.
     */
    @Column(name = "correct")
    private boolean correct;

    /**
     * Solution default constructor.
     */
    public Solution() {}

    /**
     * Solution constructor specifying all required fields.
     *
     * @param room   The room.
     * @param player The player.
     * @param word   The word.
     * @param input  The player's input.
     */
    public Solution(Room room, User player, Word word, String input) {
        this.setRoom(room);
        this.setPlayer(player);
        this.setWord(word);
        this.setInput(input);
    }

    /**
     * Returns the solution's ID.
     *
     * @return The solution's ID.
     */
    @JsonIgnore
    public Long getId() {
        return id;
    }

    /**
     * Returns the player that uploaded the solution.
     *
     * @return The player that uploaded the solution.
     */
    @JsonIgnore
    public User getPlayer() {
        return player;
    }

    /**
     * Sets the player that uploaded the solution.
     *
     * @param player The player that uploaded the solution.
     */
    public void setPlayer(User player) {
        this.player = player;
    }

    /**
     * Returns the word this is a solution to.
     *
     * @return The word this is a solution to.
     */
    public Word getWord() {
        return word;
    }

    /**
     * Sets the word this is a solution to.
     *
     * @param word The word this is a solution to.
     */
    public void setWord(Word word) {
        this.word = word;
    }

    /**
     * Returns the player's input.
     *
     * @return The player's input.
     */
    public String getInput() {
        return input;
    }

    /**
     * Sets the player's input and evaluates the solution.
     *
     * @param input The player's input.
     */
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

    /**
     * Returns whether the solution is correct.
     *
     * @return Whether the solution is correct.
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Sets whether the solution is correct.
     *
     * @param correct Whether the solution is correct.
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * Returns the room this solution was uploaded to.
     *
     * @return The room this solution was uploaded to.
     */
    @JsonIgnore
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room this solution is uploaded to.
     *
     * @param room The room this solution is uploaded to.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Returns the list of translations of the word.
     *
     * @return The list of translations of the word.
     */
    public List<String> getExpected() {
        return word.getTranslationStrings(room.getLanguageTo());
    }

}
