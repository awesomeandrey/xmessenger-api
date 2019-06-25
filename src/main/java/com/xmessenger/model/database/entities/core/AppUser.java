package com.xmessenger.model.database.entities.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmessenger.model.database.converters.RoleCodeConverter;
import com.xmessenger.model.database.entities.enums.Role;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

@javax.persistence.Entity
@Table(name = "xm_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @Pattern(regexp = "^[a-zA-Z ]{2,45}$", message = "Name is not correct")
    private String name;

    @JsonIgnore
    private byte[] picture;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,25}$", message = "Username is not correct")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(regexp = "^.{4,}$", message = "Password in wrong")
    private String password;

    @Column(name = "external")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean external;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "active")
    private Boolean active;

    @Column(name = "role_code")
    @Convert(converter = RoleCodeConverter.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Role> roles;

    @Column(name = "last_login")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastLogin;

    @Column(name = "email_address")
    @Email(message = "Email should be valid")
    private String email;

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

    public Boolean isExternal() {
        return this.external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppUser() {
        this.active = true;
        this.roles = new HashSet<>();
        this.roles.add(Role.ROLE_USER);
        this.external = false;
    }

    public AppUser(Integer uid) {
        this();
        this.id = uid;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", external=" + external +
                ", active=" + active +
                ", roles=" + roles +
                ", lastLogin=" + lastLogin +
                ", email='" + email + '\'' +
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