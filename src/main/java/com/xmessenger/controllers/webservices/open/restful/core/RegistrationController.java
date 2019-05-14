package com.xmessenger.controllers.webservices.open.restful.core;

import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.validator.UserValidationResult;
import com.xmessenger.model.services.core.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@OpenResource
public class RegistrationController {
    private final UserService userService;
    private final UserValidator validator;

    @Autowired
    public RegistrationController(UserService userService, UserValidator validator) {
        this.validator = validator;
        this.userService = userService;
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public AppUser signUp(@RequestBody AppUser user) throws UserService.UserException {
        return this.userService.registerUser(user);
    }

    @RequestMapping(value = "/sign-up/username", method = RequestMethod.POST)
    public UserValidationResult isUsernameUnique(@RequestBody String username) {
        username = username.replaceAll("\"", "");
        return this.validator.isUsernameUnique(username);
    }
}