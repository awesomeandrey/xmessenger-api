package com.xmessenger.controllers.webservices.secured.resftul.admin;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.entities.decorators.Indicator;
import com.xmessenger.model.services.core.IndicatorService;
import com.xmessenger.model.services.core.chatter.ChattingService;
import com.xmessenger.model.services.core.request.RequestService;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.exceptions.UserNotFoundException;
import com.xmessenger.model.services.core.security.RawCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(WebSecurityConfig.ADMIN_API_BASE_PATH)
public class AdministrationController {
    private final ContextUserHolder contextUserHolder;
    private final UserService userService;
    private final ChattingService chattingService;
    private final RequestService requestService;
    private final IndicatorService indicatorService;

    @Autowired
    public AdministrationController(ContextUserHolder contextUserHolder, UserService userService, ChattingService chattingService, RequestService requestService, IndicatorService indicatorService) {
        this.contextUserHolder = contextUserHolder;
        this.userService = userService;
        this.chattingService = chattingService;
        this.requestService = requestService;
        this.indicatorService = indicatorService;
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public List<Indicator> retrieveUsersOnline() {
        return this.indicatorService.getIndicators();
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    public void changeUserProfileInfo(@Valid @RequestBody AppUser appUser, HttpServletResponse response) throws IOException {
        try {
            this.userService.changeProfileInfo(appUser);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public void deleteUser(@RequestBody AppUser appUser, HttpServletResponse response) throws IOException {
        try {
            appUser = this.performPrimaryValidation(appUser);
            this.requestService.deleteRequestsAll(appUser);
            this.chattingService.deleteChatsAll(appUser);
            this.userService.deleteUser(appUser);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    @RequestMapping(value = "/resetUser", method = RequestMethod.PUT)
    public RawCredentials resetUserPassword(@RequestBody AppUser appUser, HttpServletResponse response) throws IOException {
        try {
            appUser = this.performPrimaryValidation(appUser);
            return this.userService.resetPassword(appUser);
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return null;
        }
    }

    private AppUser performPrimaryValidation(AppUser appUser) throws IllegalArgumentException {
        appUser = this.userService.lookupUser(appUser);
        if (appUser == null) {
            throw new UserNotFoundException(appUser);
        } else if (appUser.getId().equals(this.contextUserHolder.getContextUserId())) {
            throw new IllegalArgumentException("You cannot delete yourself.");
        }
        return appUser;
    }
}