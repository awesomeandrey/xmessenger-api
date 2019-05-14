package com.xmessenger.controllers.webservices.open.restful.oauth.gmail;

import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.async.AsynchronousService;
import com.xmessenger.model.services.user.UserService;
import com.xmessenger.model.services.user.oauth.gmail.GmailAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;

/**
 * OAuth 2.0 - Implicit Grant Flow;
 * Resource link: [https://tools.ietf.org/html/rfc6749#section-1.3.2]
 */
@CrossOrigin
@OpenResource
public class GmailAuthenticationController {
    private final GmailAuthenticator gmailAuthenticator;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AsynchronousService asynchronousService;

    @Autowired
    public GmailAuthenticationController(GmailAuthenticator gmailAuthenticator, UserService userService, TokenProvider tokenProvider, AsynchronousService asynchronousService) {
        this.gmailAuthenticator = gmailAuthenticator;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.asynchronousService = asynchronousService;
    }

    @RequestMapping(value = "/oauth/gmail/composeTokenUrl")
    public URL composeTokenUrl(@RequestParam(name = "host") String host) throws Exception {
        return this.gmailAuthenticator
                .urlBuilder()
                .requestAccessToken(host);
    }

    @RequestMapping(value = "/oauth/gmail/login", method = RequestMethod.POST)
    public void authenticateUser(@RequestBody String accessToken, HttpServletResponse response) throws Exception {
        accessToken = accessToken.replaceAll("\"", "");
        AppUser appUser = this.authenticateUser(accessToken);
        String token = this.tokenProvider.generateToken(appUser);
        this.tokenProvider.addTokenToResponse(response, token);
    }

    private AppUser authenticateUser(String accessToken) throws Exception {
        String username = this.gmailAuthenticator.getUsername(accessToken);
        AppUser user = this.userService.lookupUser(username);
        if (user == null) {
            user = this.gmailAuthenticator.composeUser(username, accessToken);
            this.userService.registerUser(user);
        } else if (!user.isActive()) {
            user.setActive(true);
            this.userService.changeProfileInfo(user);
        }
        this.asynchronousService.renewLastLoginByUsername(username);
        return user;
    }
}