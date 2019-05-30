package com.xmessenger.controllers.webservices.secured.resftul.admin;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.IndicatorService;
import com.xmessenger.model.services.core.chatter.ChattingService;
import com.xmessenger.model.services.core.request.RequestService;
import com.xmessenger.model.services.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(WebSecurityConfig.ADMIN_API_BASE_PATH)
public class AdministrationController {
    private final UserService userService;
    private final ChattingService chattingService;
    private final RequestService requestService;
    private final IndicatorService indicatorService;

    @Autowired
    public AdministrationController(UserService userService, ChattingService chattingService, RequestService requestService, IndicatorService indicatorService) {
        this.userService = userService;
        this.chattingService = chattingService;
        this.requestService = requestService;
        this.indicatorService = indicatorService;
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public Set<AppUserIndicator> retrieveUsersOnline() {
        return this.indicatorService.getIndicators();
    }

    @RequestMapping(value = "/deactivateUser", method = RequestMethod.DELETE)
    public void deactivateUser(@RequestBody AppUser appUser, HttpServletResponse response) throws IOException {
        appUser = this.userService.lookupUser(appUser);
        if (appUser == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User was not found.");
            return;
        }
        this.requestService.deleteRequestsAll(appUser);
        this.chattingService.deleteChatsAll(appUser);
        this.userService.deleteUser(appUser);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @RequestMapping(value = "/resetUser", method = RequestMethod.PUT)
    public void resetUserPassword() {
        // TODO - future release (email notification support);
    }
}