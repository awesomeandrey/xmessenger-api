package com.xmessenger.model.services.user;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.security.CredentialsService;
import com.xmessenger.model.services.user.security.RawCredentials;
import com.xmessenger.model.services.user.dao.UserDAO;
import com.xmessenger.model.services.user.validator.UserValidationResult;
import com.xmessenger.model.services.user.validator.UserValidator;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFlowExecutor {
    private final UserDAO userDAO;
    private final UserValidator userValidator;
    private final CredentialsService credentialsService;

    @Autowired
    public UserFlowExecutor(UserDAO userDAO, UserValidator userValidator, CredentialsService credentialsService) {
        this.userDAO = userDAO;
        this.userValidator = userValidator;
        this.credentialsService = credentialsService;
    }

    public AppUser lookupUser(RawCredentials rawCredentials) throws UserException {
        AppUser foundUser = this.userDAO.getUserByUsername(rawCredentials.getUsername());
        if (foundUser == null || !foundUser.isActive() || !this.credentialsService.matchesPassword(rawCredentials.getPassword(), foundUser)) {
            throw new UserException("User with such credentials was not found.");
        }
        return foundUser;
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
    }

    public class UserException extends Exception {
        public UserException(String m) {
            super(m);
        }
    }
}
