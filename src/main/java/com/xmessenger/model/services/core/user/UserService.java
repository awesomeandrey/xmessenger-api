package com.xmessenger.model.services.core.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.dao.decorators.QueryParams;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final CredentialsService credentialsService;

    @Autowired
    public UserService(UserDAO userDAO, CredentialsService credentialsService) {
        this.userDAO = userDAO;
        this.credentialsService = credentialsService;
    }

    public AppUser lookupUser(String username) {
        return this.userDAO.getUserByUsername(username);
    }

    public AppUser lookupUser(AppUser appUser) {
        AppUser foundUser = null;
        if (Utility.isNotBlank(appUser.getId())) {
            foundUser = this.userDAO.getUserById(appUser.getId());
        }
        if (foundUser == null && Utility.isNotBlank(appUser.getUsername())) {
            foundUser = this.lookupUser(appUser.getUsername());
        }
        return foundUser;
    }

    public List<AppUser> search(QueryParams queryParams) {
        return this.userDAO.search(queryParams);
    }

    public AppUser registerUser(@Valid AppUser userToRegister) {
        String encodedPwd = this.credentialsService.encodePassword(userToRegister.getPassword());
        userToRegister.setPassword(encodedPwd);
        return this.userDAO.create(userToRegister);
    }

    public AppUser changeProfileInfo(@Valid AppUser updatedUser) {
        AppUser persistedUser = this.lookupUser(updatedUser);
        this.mergeProperties(persistedUser, updatedUser);
        return this.userDAO.update(persistedUser);
    }

    public AppUser changePassword(AppUser appUser, String newRawPassword) {
        String encodedPassword = this.credentialsService.encodePassword(newRawPassword);
        appUser.setPassword(encodedPassword);
        return this.userDAO.update(appUser);
    }

    public void deleteUser(AppUser appUser) {
        this.userDAO.deleteUser(appUser);
    }

    //******************************************************************************************************************

    private void mergeProperties(AppUser persistedUser, AppUser updatedUser) {
        if (Utility.isNotBlank(updatedUser.getName())) {
            persistedUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPicture() != null) {
            persistedUser.setPicture(updatedUser.getPicture());
        }
        if (updatedUser.isActive() != null) {
            persistedUser.setActive(updatedUser.isActive());
        }
        if (updatedUser.getLastLogin() != null) {
            persistedUser.setLastLogin(updatedUser.getLastLogin());
        }
        if (Utility.isNotBlank(updatedUser.getEmail())) {
            persistedUser.setEmail(updatedUser.getEmail());
        }
    }
}
