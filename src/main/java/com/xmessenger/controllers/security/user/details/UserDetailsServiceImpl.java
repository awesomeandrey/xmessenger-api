package com.xmessenger.controllers.security.user.details;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.UserService;
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
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.userService.lookupUser(username);
        return new User(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }

    public void refreshLastLogin(String username) {
        AppUser appUser = this.userService.lookupUser(username);
        this.refreshLastLogin(appUser);
    }

    public void refreshLastLogin(AppUser user) {
        try {
            user.setLastLogin(new Date());
            this.userService.changeProfileInfo(user);
        } catch (UserDAO.UserNotFoundException | UserService.UserException e) {
            System.err.println("Could not set 'last_login'.");
            e.printStackTrace();
        }
    }
}