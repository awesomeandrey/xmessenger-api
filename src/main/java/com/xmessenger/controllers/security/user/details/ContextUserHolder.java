package com.xmessenger.controllers.security.user.details;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ContextUserHolder {
    private final UserDAO userDAO;

    @Autowired
    public ContextUserHolder(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AppUser getContextUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (auth.getPrincipal() instanceof String) {
            // Bearer Token flow;
            username = (String) auth.getPrincipal();
        } else if (auth.getPrincipal() instanceof UserDetails) {
            // Basic Authentication flow;
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return this.userDAO.getUserByUsername(username);
    }

    public Integer getContextUserId() {
        return this.getContextUser().getId();
    }
}