package com.viktorban.wlgame.model;

import javax.persistence.*;

@Entity
@Table(name = "solutions")
public class Solution {

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private User player;

    @OneToOne
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;

    @Column(name = "input")
    private String input;

    @Column(name = "correct")
    private boolean correct;

    public Solution() {}

    public Solution(User player, Word word, String input) {
        this.setPlayer(player);
        this.setWord(word);
        this.setInput(input);

    }

    public long getId() {
        return id;
    }

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
    }

    public boolean isCorrect() {
        return correct;
    }

    public void evaluate(Language language) {
        correct = false;
        word.getTranslations(language).stream().filter(translation -> translation.getWord().toLowerCase().equals(input)).forEach(translation -> correct = true);
    }

}
