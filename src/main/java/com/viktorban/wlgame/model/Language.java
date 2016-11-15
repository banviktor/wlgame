package com.viktorban.wlgame.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a language entity.
 */
@Entity
@Table(name = "languages")
public class Language {

    /**
     * The country code, also used as the unique indentifier.
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * The language name.
     */
    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    /**
     * Language default constructor.
     */
    public Language() {}

    /**
     * Language constructor specifying all the required fields.
     *
     * @param id   The language identifier.
     * @param name The language name.
     */
    public Language(String id, String name) {
        this.id = id;
        this.setName(name);
    }

    /**
     * Returns the language identifier.
     *
     * @return The language identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the language name.
     *
     * @return The language name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the language name.
     * @param name The language name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Language) {
            Language otherLanguage = (Language) obj;
            return otherLanguage.id.equals(id);
        }
        return false;
    }

}
