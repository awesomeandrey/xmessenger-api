package com.xmessenger.controllers.webservices.open.restful.core;

import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.validator.UserValidator;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@OpenResource
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public AppUser signUp(@Valid @RequestBody AppUser user, HttpServletResponse response) throws IOException {
        UserValidator.Result result = this.isUsernameUnique(user.getUsername());
        if (result.isValid()) {
            try {
                return this.userService.registerUser(user);
            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, result.getErrorMessage());
        }
        return null;
    }

    @RequestMapping(value = "/sign-up/username", method = RequestMethod.POST)
    public UserValidator.Result isUsernameUnique(@RequestBody @NotBlank String username) {
        username = username.replaceAll("\"", "");
        if (this.isUsernameOccupied(username)) {
            return new UserValidator.Result("Username is already in use.");
        }
        return new UserValidator.Result();
    }

    private boolean isUsernameOccupied(String param) {
        return this.userService.lookupUser(param) != null;
    }
}