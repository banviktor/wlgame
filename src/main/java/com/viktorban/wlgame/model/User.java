package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktorban.wlgame.Application;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user entity.
 * Maps users to the "users" database table.
 */
@Entity
@Table(name = "users")
public class User extends ResourceSupport {

    /**
     * Automatically generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
   private Long id;

    /**
     * User's name.
     *
     * Can only be set during creation.
     */
    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    /**
     * Hashed password.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "pass_hash", nullable = false)
    private String password;

    /**
     * Whether the user is enabled.
     */
    @Column(name = "status")
    private boolean enabled;

    /**
     * The list of Roles the user has.
     *
     * @see com.viktorban.wlgame.model.Role
     */
    @ManyToMany()
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name="uid", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="rid", referencedColumnName="id")
    )
    private List<Role> roles;

    /**
     * The list of rooms the user has ever joined.
     */
    @OneToMany(mappedBy = "player")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RoomPlayer> joinedRooms;

    /**
     * User default constructor.
     */
    public User() {
        this.enabled = true;
        this.roles = new ArrayList<>();
        this.joinedRooms = new ArrayList<>();
    }

    /**
     * User constructor specifying all the required fields.
     *
     * @param name      The username.
     * @param password  Plaintext password.
     * @param enabled   User status.
     */
    public User(String name, String password, boolean enabled) {
        this();
        this.setName(name);
        this.setPassword(password);
        this.setEnabled(enabled);
    }

    /**
     * Returns the user ID.
     *
     * @return The user ID.
     */
    @JsonProperty("id")
    public Long getUserId() {
        return id;
    }

    /**
     * Returns the username.
     *
     * @return The username.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the username.
     *
     * @param name The username to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the hashed password.
     *
     * @return The hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password Plaintext password to set.
     */
    public void setPassword(String password) {
        this.password = Application.getPasswordEncoder().encode(password);
    }

    /**
     * Returns whether the user is enabled.
     *
     * @return Whether the user is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the user status.
     *
     * @param enabled The user status to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the list of roles the user has.
     *
     * @return The list of roles the user has.
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles.
     *
     * @param roles List of roles to set.
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Adds a role to the user's list of roles.
     *
     * @param role The role to add.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    @JsonIgnore
    public List<RoomPlayer> getJoinedRooms() {
        return joinedRooms;
    }

    public void setJoinedRooms(List<RoomPlayer> joinedRooms) {
        this.joinedRooms = joinedRooms;
    }

    /**
     * Returns the first non-ended room that the player has joined.
     *
     * @return The first non-ended room that the player has joined.
     */
    public Long getActiveRoomId() {
        for (RoomPlayer joinedRoom : joinedRooms) {
            if (joinedRoom.getRoom().getState() != Room.RoomState.ENDED) {
                return joinedRoom.getRoom().getRoomId();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User otherUser = (User) obj;
            return id == otherUser.id;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User (id: " + getUserId() + ", name: " + getName() + ")";
    }

}
