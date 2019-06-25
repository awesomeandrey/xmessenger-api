package com.xmessenger.model.services.core.security;

import com.xmessenger.model.database.entities.core.AppUser;

public class RawCredentials {
    private String username;
    private String password;
    private String newPassword;

    public RawCredentials() {
    }

    public RawCredentials(AppUser appUser) {
        this.username = appUser.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "RawCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
