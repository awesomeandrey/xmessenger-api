package com.xmessenger.model.services.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.chatter.core.RelationService;
import com.xmessenger.model.services.user.dao.QueryParams;
import com.xmessenger.model.services.user.security.CredentialsService;
import com.xmessenger.model.services.user.security.RawCredentials;
import com.xmessenger.model.services.user.dao.UserDAO;
import com.xmessenger.model.services.user.validator.UserValidationResult;
import com.xmessenger.model.services.user.validator.UserValidator;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        AppUser foundUser = this.userDAO.getUserByUsername(rawCredentials.getUsername());
        if (foundUser == null || !foundUser.isActive() || !this.credentialsService.matchesPassword(rawCredentials.getPassword(), foundUser)) {
            throw new UserException("User with such credentials was not found.");
        }
        return foundUser;
    }

    public AppUser lookupUser(String username) {
        return this.userDAO.getUserByUsername(username);
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

    public AppUser registerUser(AppUser userToRegister) throws UserException {
        UserValidationResult validationResult = this.userValidator.validateOnRegistration(userToRegister);
        if (!validationResult.isValid()) {
            throw new UserException(validationResult.getErrorMessage());
        }
        this.credentialsService.encodePassword(userToRegister);
        return this.userDAO.create(userToRegister);
    }

    /**
     * Allowed fields to modify: Name, Picture, IsActive.
     *
     * @param updatedUser - User record with updated profile info.
     * @return Modified User record;
     * @throws UserException
     * @throws UserDAO.UserNotFoundException
     */
    public AppUser changeProfileInfo(AppUser updatedUser) throws UserException, UserDAO.UserNotFoundException {
        UserValidationResult validationResult = this.userValidator.validateOnProfileChange(updatedUser);
        if (!validationResult.isValid()) {
            throw new UserException(validationResult.getErrorMessage());
        }
        AppUser persistedUser = this.userDAO.getUserById(updatedUser.getId());
        this.mergeProperties(persistedUser, updatedUser);
        return this.userDAO.update(persistedUser);
    }

    public AppUser changePassword(AppUser user, RawCredentials rawCredentials) throws UserException, UserDAO.UserNotFoundException {
        UserValidationResult validationResult = this.userValidator.validateOnPasswordChange(user, rawCredentials);
        if (!validationResult.isValid()) {
            throw new UserException(validationResult.getErrorMessage());
        }
        AppUser persistedUser = this.userDAO.getUserById(user.getId());
        persistedUser.setPassword(rawCredentials.getNewPassword());
        this.credentialsService.encodePassword(persistedUser);
        return this.userDAO.update(persistedUser);
    }

    public void renewLastLogin(String username) {
        try {
            AppUser appUser = this.lookupUser(username);
            appUser.renewLastLoginDate();
            this.changeProfileInfo(appUser);
        } catch (UserService.UserException | UserDAO.UserNotFoundException e) {
            System.err.println(">>> Could not set 'last_login'.");
            e.printStackTrace();
        }
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
    }

    public class UserException extends Exception {
        public UserException(String m) {
            super(m);
        }
    }
}
