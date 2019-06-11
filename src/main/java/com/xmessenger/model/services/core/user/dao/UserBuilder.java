package com.xmessenger.model.services.core.user.dao;

import com.xmessenger.model.database.entities.core.AppUser;

public class UserBuilder {
    private String name;
    private String username;
    private String password;
    private String email;
    private byte[] picture;
    private Boolean loggedExternally;
    private Boolean active;

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UserBuilder() {
        this.active = true;
        this.loggedExternally = false;
    }

    public UserBuilder(String name) {
        this();
        this.name = name;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPicture(byte[] picture) {
        this.picture = picture;
        return this;
    }

    public UserBuilder withLoggedExternally(boolean loggedExternally) {
        this.loggedExternally = loggedExternally;
        return this;
    }

    public UserBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }

    public AppUser build() {
        AppUser user = new AppUser();
        user.setName(this.name);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setPicture(this.picture);
        user.setLoggedExternally(this.loggedExternally);
        user.setActive(this.active);
        return user;
    }
}
