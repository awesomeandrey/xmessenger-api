package com.xmessenger.controllers.webservices.open.restful.oauth.gmail;

public class GmailAccount {
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getUsername() {
        return emailAddress.split("@")[0];
    }

    public String getPassword(String gmailUserId) {
        return gmailUserId.substring(0, Math.min(gmailUserId.length(), 10)); // first 10 characters;
    }

    public GmailAccount(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
