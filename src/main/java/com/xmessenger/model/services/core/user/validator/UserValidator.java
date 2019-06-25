package com.xmessenger.model.services.core.user.validator;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.security.CredentialsService;
import com.xmessenger.model.services.core.user.security.decorators.RawCredentials;
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

    public Result validateOnPasswordChange(AppUser userToValidate, RawCredentials rawCredentials) {
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
