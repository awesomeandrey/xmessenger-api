package com.xmessenger.controllers.security.user.details;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.loadAppUserByUsername(username);
        return new User(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }

    public void refreshLastLogin(String username) {
        try {
            AppUser appUser = this.loadAppUserByUsername(username);
            appUser.setLastLogin(new Date());
            this.userDAO.update(appUser);
        } catch (UserDAO.UserNotFoundException e) {
            System.err.println("Could not set 'last_login'.");
            e.printStackTrace();
        }
    }

    private AppUser loadAppUserByUsername(String username) {
        AppUser appUser = this.userDAO.getUserByUsername(username);
        if (appUser == null || !appUser.isActive()) {
            throw new UsernameNotFoundException(username);
        }
        return appUser;
    }
}