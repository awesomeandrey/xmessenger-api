package com.xmessenger.controllers.security.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.userDAO.getUserByUsername(username);
        if (appUser == null || !appUser.isActive()) {
            throw new UsernameNotFoundException(username);
        }
        return new User(appUser.getUsername(), appUser.getPassword(), this.getAuthorities(appUser));
    }

    private Set getAuthorities(AppUser appUser) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        appUser.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(this.ROLE_PREFIX.concat(role.name())));
        });
        return authorities;
    }
}