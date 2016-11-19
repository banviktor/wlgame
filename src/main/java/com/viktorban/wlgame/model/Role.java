package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Represents a user role.
 */
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    /**
     * The default player role.
     */
    public static Role ROLE_PLAYER;

    /**
     * The default moderator role.
     */
    public static Role ROLE_MODERATOR;

    /**
     * The default administrator role.
     */
    public static Role ROLE_ADMINISTRATOR;

    /**
     * Automatically generated role identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
   private Long id;

    /**
     * The role's name.
     */
    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    /**
     * Role default constructor.
     */
    public Role() {}

    /**
     * Role constructor specifying all the required fields.
     *
     * @param name Role name.
     */
    public Role(String name) {
        this.setName(name);
    }

    /**
     * Returns the role ID.
     *
     * @return The role ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the role name.
     *
     * @return The role name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the role name.
     *
     * @param name The role name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the role name.
     *
     * @return The role name.
     */
    @Override
    @JsonIgnore
    public String getAuthority() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            Role otherRole = (Role) obj;
            return id == otherRole.id;
        }
        return false;
    }

}
