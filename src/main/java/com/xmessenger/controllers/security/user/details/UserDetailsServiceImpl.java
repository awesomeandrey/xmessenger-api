package com.xmessenger.controllers.security.user.details;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.userService.lookupUser(username);
        if (!appUser.isActive()) return null;
        return new User(appUser.getUsername(), appUser.getPassword(), appUser.getRoles());
    }
}