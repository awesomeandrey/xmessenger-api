package com.xmessenger.model.services.core.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.security.RawCredentials;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserValidator {
    private final UserDAO userDAO;
    private final CredentialsService credentialsService;

    @Autowired
    public UserValidator(UserDAO userDAO, CredentialsService credentialsService) {
        this.userDAO = userDAO;
        this.credentialsService = credentialsService;
    }

    public UserValidationResult validateOnRegistration(AppUser userToValidate) {
        UserValidationResult validationResult = this.checkRequiredProperties(userToValidate);
        if (!validationResult.isValid()) {
            return validationResult;
        }
        validationResult = this.isUsernameUnique(userToValidate.getUsername());
        if (!validationResult.isValid()) {
            return validationResult;
        }
        return new UserValidationResult(true);
    }

    public UserValidationResult validateOnProfileChange(AppUser userToValidate) {
        if (Utility.isBlank(userToValidate.getId())) {
            return new UserValidationResult(false, "No User ID provided.");
        }
        String name = userToValidate.getName();
        if (Utility.isNotBlank(name) && !this.isNameCorrect(name)) {
            return new UserValidationResult(false, "Invalid 'Name' field value.");
        }
        return new UserValidationResult(true);
    }

    public UserValidationResult validateOnPasswordChange(AppUser userToValidate, RawCredentials rawCredentials) {
        final String oldRawPassword = rawCredentials.getPassword(), newRawPassword = rawCredentials.getNewPassword();
        if (!this.isRawPasswordCorrect(newRawPassword)) {
            return new UserValidationResult(false, "Invalid 'Password' field value.");
        }
        if (!this.credentialsService.matchesPassword(oldRawPassword, userToValidate)) {
            return new UserValidationResult(false, "Password is not confirmed.");
        }
        if (this.credentialsService.matchesPassword(newRawPassword, userToValidate)) {
            return new UserValidationResult(false, "Password cannot be the same.");
        }
        return new UserValidationResult(true);
    }

    public UserValidationResult isUsernameUnique(String username) {
        if (this.userDAO.getUserByUsername(username) != null) {
            return new UserValidationResult(false, "Username is already in use.");
        }
        return new UserValidationResult(true);
    }

    private UserValidationResult checkRequiredProperties(AppUser userToCheck) {
        UserValidationResult validationResult = new UserValidationResult(true);
        if (!this.isNameCorrect(userToCheck.getName())
                || !this.isUsernameCorrect(userToCheck.getUsername())
                || !this.isRawPasswordCorrect(userToCheck.getPassword())) {
            validationResult = new UserValidationResult(false, "Wrong value in one of required fields.");
        }
        return validationResult;
    }

    private boolean isNameCorrect(String name) {
        return Utility.isNotBlank(name) && name.matches("^[a-zA-Z ]{2,45}$");
    }

    private boolean isUsernameCorrect(String username) {
        return Utility.isNotBlank(username) && username.matches("^[a-zA-Z0-9_-]{4,25}$");
    }

    private boolean isRawPasswordCorrect(String password) {
        return Utility.isNotBlank(password) && password.matches("[^ ]{4,16}");
    }
}
