package com.xmessenger.model.services.core.user.validator;

public class UserValidationResult {
    private boolean isValid;
    private String errorMessage;

    public UserValidationResult() {
        this.isValid = true;
    }

    public UserValidationResult(String errorMessage) {
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
