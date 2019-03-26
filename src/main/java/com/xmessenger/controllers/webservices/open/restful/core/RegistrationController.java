package com.xmessenger.controllers.webservices.open.restful.core;

import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.user.UserFlowExecutor;
import com.xmessenger.model.services.user.validator.UserValidationResult;
import com.xmessenger.model.services.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@OpenResource
public class RegistrationController {
    private final UserFlowExecutor flowExecutor;
    private final UserValidator validator;

    @Autowired
    public RegistrationController(UserFlowExecutor flowExecutor, UserValidator validator) {
        this.validator = validator;
        this.flowExecutor = flowExecutor;
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public ApplicationUser signUp(@RequestBody ApplicationUser user) throws UserFlowExecutor.UserException {
        return this.flowExecutor.registerUser(user);
    }

    @RequestMapping(value = "/sign-up/username", method = RequestMethod.POST)
    public UserValidationResult isUsernameUnique(@RequestBody String username) {
        username = username.replaceAll("\"", "");
        return this.validator.isUsernameUnique(username);
    }
}