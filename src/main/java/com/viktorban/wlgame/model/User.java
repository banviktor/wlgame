package com.viktorban.wlgame.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viktorban.wlgame.Application;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "pass_hash", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "status")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="users_roles",
            joinColumns=@JoinColumn(name="uid", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="rid", referencedColumnName="id")
    )
    private List<Role> roles;

    public User() {
        this.enabled = true;
        this.roles = new ArrayList<>();
    }

    public User(String name, String password, String email, boolean enabled) {
        this();
        this.setName(name);
        this.setPassword(password);
        this.setEmail(email);
        this.setEnabled(enabled);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Application.getPasswordEncoder().encode(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }
}
