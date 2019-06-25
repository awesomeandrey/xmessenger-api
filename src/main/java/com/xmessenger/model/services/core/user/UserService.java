package com.xmessenger.model.services.core.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.chatter.RelationService;
import com.xmessenger.model.services.core.user.dao.QueryParams;
import com.xmessenger.model.services.core.user.exceptions.UserNotFoundException;
import com.xmessenger.model.services.core.security.CredentialsService;
import com.xmessenger.model.services.core.security.RawCredentials;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import com.xmessenger.model.services.core.user.validator.UserValidationResult;
import com.xmessenger.model.services.core.user.validator.UserValidator;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final UserValidator userValidator;
    private final CredentialsService credentialsService;
    private final RelationService relationService;

    @Autowired
    public UserService(UserDAO userDAO, UserValidator userValidator, CredentialsService credentialsService, RelationService relationService) {
        this.userDAO = userDAO;
        this.userValidator = userValidator;
        this.credentialsService = credentialsService;
        this.relationService = relationService;
    }

    public AppUser lookupUser(RawCredentials rawCredentials) throws UserException {
        AppUser foundUser = this.lookupUser(rawCredentials.getUsername());
        if (foundUser == null || !foundUser.isActive() || !this.credentialsService.matchesPassword(rawCredentials.getPassword(), foundUser.getPassword())) {
            throw new UserException("User with such credentials was not found.");
        }
        return foundUser;
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

    public List<AppUser> lookupUsers(QueryParams queryParams) {
        return this.userDAO.search(queryParams);
    }

    public Map<Integer, AppUser> findFellows(AppUser user) {
        Map<Integer, AppUser> fellowsMap = new HashMap<>();
        this.relationService.getUserRelations(user).values().forEach(relation -> {
            AppUser fellow = this.relationService.getFellowFromRelation(user, relation);
            fellowsMap.put(fellow.getId(), fellow);
        });
        return fellowsMap;
    }

    public AppUser registerUser(@Valid AppUser userToRegister) {
        String encodedPwd = this.credentialsService.encodePassword(userToRegister.getPassword());
        userToRegister.setPassword(encodedPwd);
        return this.userDAO.create(userToRegister);
    }

    public AppUser changeProfileInfo(@Valid AppUser updatedUser) throws UserNotFoundException {
        AppUser persistedUser = this.lookupUser(updatedUser);
        this.mergeProperties(persistedUser, updatedUser);
        return this.userDAO.update(persistedUser);
    }

    public AppUser changePassword(AppUser user, RawCredentials rawCredentials) throws UserException, UserNotFoundException {
        UserValidationResult validationResult = this.userValidator.validateOnPasswordChange(user, rawCredentials);
        if (!validationResult.isValid()) {
            throw new UserException(validationResult.getErrorMessage());
        }
        AppUser persistedUser = this.lookupUser(user);
        String encodedPassword = this.credentialsService.encodePassword(rawCredentials.getNewPassword());
        persistedUser.setPassword(encodedPassword);
        return this.userDAO.update(persistedUser);
    }

    public RawCredentials resetPassword(AppUser appUser) throws UserNotFoundException {
        String newRawPassword = this.credentialsService.generateRandomPassword(8);
        String encodedPassword = this.credentialsService.encodePassword(newRawPassword);
        appUser.setPassword(encodedPassword);
        this.userDAO.update(appUser);
        RawCredentials rawCredentials = new RawCredentials(appUser);
        rawCredentials.setNewPassword(newRawPassword);
        return rawCredentials;
    }

    public void renewLastLogin(AppUser appUser) {
        try {
            appUser.setLastLogin(new Date());
            this.changeProfileInfo(appUser);
        } catch (UserNotFoundException e) {
            System.err.println(">>> Could not set 'last_login'. " + e.getMessage());
        }
    }

    public void deleteUser(AppUser appUser) {
        this.userDAO.deleteUser(appUser);
    }

    //******************************************************************************************************************

    /**
     * Allowed fields to modify: Name, Picture, IsActive, Email.
     *
     * @param updatedUser - User record with updated profile info.
     * @return Modified User record;
     * @throws UserException
     * @throws UserNotFoundException
     */
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

    public class UserException extends Exception {
        public UserException(String m) {
            super(m);
        }
    }
}
