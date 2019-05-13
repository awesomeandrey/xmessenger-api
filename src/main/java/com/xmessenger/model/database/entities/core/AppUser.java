package com.xmessenger.model.database.entities.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmessenger.model.database.converters.RoleCodeConverter;
import com.xmessenger.model.database.entities.Role;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "xm_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    private String name;

    @JsonIgnore
    private byte[] picture;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_logged_externally")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean loggedExternally;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "role_code")
    @Convert(converter = RoleCodeConverter.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Role> roles;

    @Column(name = "last_login")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastLogin;

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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public AppUser() {
        this.active = true;
        this.roles = new HashSet<>();
        this.roles.add(Role.ROLE_USER);
        this.loggedExternally = false;
    }

    public void renewLastLoginDate() {
        this.lastLogin = new Date();
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", loggedExternally=" + loggedExternally +
                ", active=" + active +
                ", roles=" + roles +
                ", lastLogin=" + lastLogin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser user = (AppUser) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username);
    }
}