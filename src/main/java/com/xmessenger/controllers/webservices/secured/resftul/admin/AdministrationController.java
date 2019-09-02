package com.xmessenger.controllers.webservices.secured.resftul.admin;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.entities.core.Indicator;
import com.xmessenger.model.services.core.user.credentials.CredentialsService;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import com.xmessenger.model.services.core.user.indicators.IndicatorService;
import com.xmessenger.model.services.core.chatter.ChattingService;
import com.xmessenger.model.services.core.request.RequestService;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.credentials.decorators.RawCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private final CredentialsService credentialsService;

    @Autowired
    public AdministrationController(ContextUserHolder contextUserHolder, UserService userService, ChattingService chattingService, RequestService requestService, IndicatorService indicatorService, CredentialsService credentialsService) {
        this.contextUserHolder = contextUserHolder;
        this.userService = userService;
        this.chattingService = chattingService;
        this.requestService = requestService;
        this.indicatorService = indicatorService;
        this.credentialsService = credentialsService;
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public List<Indicator> retrieveUsersOnline() {
        return this.indicatorService.getIndicators();
    }

    @RequestMapping(value = "/findUser", method = RequestMethod.GET)
    public AppUser lookupUser(@RequestBody AppUser appUser) {
        return this.lookupUserAndValidate(appUser);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    public void changeUserProfileInfo(@RequestBody AppUser appUser) {
        AppUser persistedUser = this.lookupUserAndValidate(appUser);
        this.userService.changeProfileInfo(persistedUser, appUser);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public void deleteUser(@RequestBody AppUser appUser) {
        appUser = this.lookupUserAndValidate(appUser);
        if (appUser.getId().equals(this.contextUserHolder.getContextUserId())) {
            throw new UnsupportedOperationException("Admin cannot delete himself.");
        }
        this.requestService.deleteRequestsAll(appUser);
        this.chattingService.deleteChatsAll(appUser);
        this.userService.deleteUser(appUser);
    }

    @RequestMapping(value = "/resetUser", method = RequestMethod.PUT)
    public RawCredentials resetUserPassword(@RequestBody AppUser appUser) {
        appUser = this.lookupUserAndValidate(appUser);
        if (appUser.isExternal()) {
            throw new UnsupportedOperationException("Externally logged users don't change password.");
        }
        String randomPassword = this.credentialsService.generateRandomPassword(8);
        appUser = this.userService.changePassword(appUser, randomPassword);
        return new RawCredentials(appUser.getUsername(), null, randomPassword);
    }

    private AppUser lookupUserAndValidate(AppUser appUser) {
        AppUser foundUser = this.userService.lookupUser(appUser);
        if (foundUser == null) {
            throw new UserDAO.UserNotFoundException(appUser);
        }
        return foundUser;
    }
}