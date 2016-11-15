package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a word.
 */
@Entity
@Table(name = "words")
public class Word {

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    /**
     * The language of the word.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id")
    private Language language;

    /**
     * The actual word.
     */
    @Column(name = "word", unique = true, nullable = false)
    private String word;

    /**
     * The equivalents of this word in other languages.
     */
    @ManyToMany()
    @JoinTable(
            name = "translations",
            joinColumns=@JoinColumn(name="word1_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="word2_id", referencedColumnName="id")
    )
    private List<Word> translations;

    /**
     * The words that map this as their translation.
     */
    @ManyToMany(mappedBy = "translations")
    private List<Word> reverseTranslations;

    /**
     * Word default constructor.
     */
    public Word() {
        translations = new ArrayList<>();
        reverseTranslations = new ArrayList<>();
    }

    /**
     * Word constructor specifying all the required fields.
     *
     * @param language The word's language.
     * @param word     The actual word.
     */
    public Word(Language language, String word) {
        this();
        this.setLanguage(language);
        this.setWord(word);
    }

    /**
     * Returns the word ID.
     *
     * @return The word ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the language of the word.
     *
     * @return The language of the word.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language of the word.
     * @param language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Returns the actual word.
     *
     * @return The actual word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets the actual word.
     *
     * @param word The actual word to set.
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Returns the combined list of translations.
     *
     * @return The combined list of translations.
     */
    public List<Word> getTranslations() {
        List<Word> combined = new ArrayList<>();
        combined.addAll(translations);
        combined.addAll(reverseTranslations);
        return combined;
    }

    /**
     * Gets the translation of the word in the given language.
     *
     * @param language The target language.
     * @return The translation, or null if not found.
     */
    public Word getTranslation(Language language) {
        for (Word translation : getTranslations()) {
            if (translation.language == language) {
                return translation;
            }
        }
        return null;
    }

    /**
     * Adds the given translation.
     *
     * @param translation The translation.
     */
    public void addTranslation(Word translation) {
        translations.add(translation);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return word;
    }

}
