package com.xmessenger.model.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "`user`")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uid")
    private Integer id;
    private String name;
    private byte[] picture;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(name = "is_logged_externally")
    private Boolean loggedExternally;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_active")
    private Boolean active;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer recordId) {
        this.id = recordId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        if (picture != null) {
            this.picture = picture;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getLoggedExternally() {
        return this.loggedExternally;
    }

    public void setLoggedExternally(Boolean loggedExternally) {
        this.loggedExternally = loggedExternally;
    }

    public ApplicationUser() {
        this.active = true;
        this.loggedExternally = false;
    }

    public boolean isHasPicture() {
        return this.picture != null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", loggedExternally=" + loggedExternally +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser user = (ApplicationUser) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username);
    }
}