package com.xmessenger.model.services.core.user.exceptions;

import com.xmessenger.model.database.entities.core.AppUser;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Integer i) {
        super("Could not find user with ID=[" + i + "].");
    }

    public UserNotFoundException(AppUser appUser) {
        this(appUser.getId());
    }
}