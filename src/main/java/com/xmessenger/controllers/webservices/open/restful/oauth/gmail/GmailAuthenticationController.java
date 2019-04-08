package com.xmessenger.controllers.webservices.open.restful.oauth.gmail;

import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.UserFlowExecutor;
import com.xmessenger.model.services.user.dao.UserDAO;
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
    private final UserFlowExecutor userFlowExecutor;
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;

    @Autowired
    public GmailAuthenticationController(GmailAuthenticator gmailAuthenticator, UserFlowExecutor userFlowExecutor, UserDAO userDAO, TokenProvider tokenProvider) {
        this.gmailAuthenticator = gmailAuthenticator;
        this.userFlowExecutor = userFlowExecutor;
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
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
        AppUser user = this.userDAO.getUserByUsername(username);
        if (user == null) {
            user = this.gmailAuthenticator.composeUser(username, accessToken);
            this.userFlowExecutor.registerUser(user);
        } else if (!user.isActive()) {
            user.setActive(true);
            this.userFlowExecutor.changeProfileInfo(user);
        }
        return user;
    }
}