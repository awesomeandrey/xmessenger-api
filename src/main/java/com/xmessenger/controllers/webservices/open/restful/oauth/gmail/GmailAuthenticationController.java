package com.xmessenger.controllers.webservices.open.restful.oauth.gmail;

import com.xmessenger.controllers.security.jwt.JwtConfig;
import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.user.UserFlowExecutor;
import com.xmessenger.model.services.user.dao.UserDAO;
import com.xmessenger.model.services.user.oauth.gmail.GmailAuthenticator;
import com.xmessenger.model.services.user.oauth.gmail.UrlBuilder;
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
    private final UrlBuilder urlBuilder;
    private final JwtConfig jwtConfig;

    @Autowired
    public GmailAuthenticationController(GmailAuthenticator gmailAuthenticator, UserFlowExecutor userFlowExecutor, UserDAO userDAO, UrlBuilder urlBuilder, JwtConfig jwtConfig) {
        this.gmailAuthenticator = gmailAuthenticator;
        this.userFlowExecutor = userFlowExecutor;
        this.userDAO = userDAO;
        this.urlBuilder = urlBuilder;
        this.jwtConfig = jwtConfig;
    }

    @RequestMapping(value = "/oauth/gmail/composeTokenUrl")
    public URL composeTokenUrl(@RequestParam(name = "host") String host) throws Exception {
        return this.urlBuilder.requestAccessToken(host);
    }

    @RequestMapping(value = "/oauth/gmail/login", method = RequestMethod.POST)
    public void authenticateUser(@RequestBody String accessToken, HttpServletResponse response) throws Exception {
        accessToken = accessToken.replaceAll("\"", "");
        String username = this.gmailAuthenticator.getUsername(accessToken);
        ApplicationUser user = this.userDAO.getUserByUsername(username);
        if (user == null) {
            user = this.gmailAuthenticator.composeUser(username, accessToken);
            user = this.userFlowExecutor.registerUser(user);
        } else if (!user.isActive()) {
            user.setActive(true);
            user = this.userFlowExecutor.changeProfileInfo(user);
        }
        String token = this.jwtConfig.composeToken(user.getUsername());
        response.addHeader(this.jwtConfig.getHeader(), this.jwtConfig.getPrefix() + token);
    }
}