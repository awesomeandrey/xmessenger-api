package com.xmessenger.controllers.security.user;

import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ContextUserRetriever {
    private final UserDAO userDAO;

    @Autowired
    public ContextUserRetriever(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ApplicationUser getContextUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        return this.userDAO.getUserByUsername(username);
    }

    public Integer getContextUserId() {
        return this.getContextUser().getId();
    }
}