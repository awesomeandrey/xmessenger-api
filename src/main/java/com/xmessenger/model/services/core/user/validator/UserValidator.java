package com.xmessenger.model.services.core.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.credentials.CredentialsService;
import com.xmessenger.model.services.core.user.credentials.decorators.RawCredentials;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserValidator {
    private final CredentialsService credentialsService;

    @Autowired
    public UserValidator(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    /**
     * Validates AppUser entity when registration is being happening.
     * Mandatory fields to check: Name, Username, Password (raw).
     *
     * @param userToValidate - AppUser entity to check.
     * @return Validation Result object.
     */
    public Result validateOnRegistration(AppUser userToValidate) {
        Result result = this.validateOnProfileChange(userToValidate);
        if (!result.isValid) return result;
        if (!this.isUsernameCorrect(userToValidate.getUsername())) {
            return new Result("Invalid 'Username' field value.");
        }
        if (!this.isRawPasswordCorrect(userToValidate.getPassword())) {
            return new Result("Invalid 'Password' field value.");
        }
        return new Result();
    }

    public Result validateOnProfileChange(AppUser userToValidate) {
        if (!this.isNameCorrect(userToValidate.getName())) {
            return new Result("Invalid 'Name' field value.");
        }
        return new Result();
    }

    public Result validateOnPasswordChange(AppUser userToValidate, RawCredentials rawCredentials) {
        if (userToValidate.isExternal()) {
            return new Result("Externally logged users don't change password.");
        }
        final String oldRawPassword = rawCredentials.getPassword(), newRawPassword = rawCredentials.getNewPassword();
        if (!this.isRawPasswordCorrect(newRawPassword)) {
            return new Result("Invalid 'Password' field value.");
        }
        if (!this.credentialsService.matchesPassword(oldRawPassword, userToValidate.getPassword())) {
            return new Result("Password is not confirmed.");
        }
        if (this.credentialsService.matchesPassword(newRawPassword, userToValidate.getPassword())) {
            return new Result("Password cannot be the same.");
        }
        return new Result();
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

    public static class Result {
        private boolean isValid;
        private String errorMessage;

        public Result() {
            this.isValid = true;
        }

        public Result(String errorMessage) {
            this.isValid = false;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
