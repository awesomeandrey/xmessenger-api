package com.xmessenger.model.services.core.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.security.CredentialsService;
import com.xmessenger.model.services.core.security.RawCredentials;
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

    public UserValidationResult validateOnPasswordChange(AppUser userToValidate, RawCredentials rawCredentials) {
        final String oldRawPassword = rawCredentials.getPassword(), newRawPassword = rawCredentials.getNewPassword();
        if (!this.isRawPasswordCorrect(newRawPassword)) {
            return new UserValidationResult(false, "Invalid 'Password' field value.");
        }
        if (!this.credentialsService.matchesPassword(oldRawPassword, userToValidate.getPassword())) {
            return new UserValidationResult(false, "Password is not confirmed.");
        }
        if (this.credentialsService.matchesPassword(newRawPassword, userToValidate.getPassword())) {
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

    private boolean isRawPasswordCorrect(String password) {
        return Utility.isNotBlank(password) && password.matches("[^ ]{4,16}");
    }
}
