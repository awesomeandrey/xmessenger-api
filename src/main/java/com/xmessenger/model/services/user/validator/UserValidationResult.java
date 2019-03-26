package com.xmessenger.model.services.user.validator;

public class UserValidationResult {
    private boolean isValid;
    private String errorMessage;

    public UserValidationResult(boolean isValid) {
        this.isValid = isValid;
        this.errorMessage = isValid ? "" : "User validation failure.";
    }

    public UserValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
