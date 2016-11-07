package com.viktorban.wlgame.model;

import javax.persistence.*;

/**
 * Represents a word.
 */
@Entity
@Table(name = "dictionary")
public class Word {

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    /**
     * The word in the original language (english).
     */
    @Column(name = "original", nullable = false)
    private String original;

    /**
     * The translated word (hungarian).
     */
    @Column(name = "translated", nullable = false)
    private String translated;

    /**
     * Returns the word ID.
     *
     * @return The word ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the word in the original language.
     *
     * @return The word in the original language.
     */
    public String getOriginal() {
        return original;
    }

    /**
     * Sets the word in the original language.
     *
     * @param original The word in the original language to set.
     * @return The word.
     */
    public Word setOriginal(String original) {
        this.original = original;
        return this;
    }

    /**
     * Returns the translated word.
     *
     * @return The translated word.
     */
    public String getTranslated() {
        return translated;
    }

    /**
     * Sets the translated word.
     *
     * @param translated The translated word to set.
     * @return The word.
     */
    public Word setTranslated(String translated) {
        this.translated = translated;
        return this;
    }

}
