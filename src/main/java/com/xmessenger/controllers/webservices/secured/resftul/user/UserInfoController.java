package com.xmessenger.controllers.webservices.secured.resftul.user;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserRetriever;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.user.UserFlowExecutor;
import com.xmessenger.model.services.user.dao.QueryParams;
import com.xmessenger.model.services.user.dao.UserDAO;
import com.xmessenger.model.services.user.security.RawCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/user")
public class UserInfoController {
    private final ContextUserRetriever contextUserRetriever;
    private final UserDAO userDAO;
    private final UserFlowExecutor flowExecutor;

    @Autowired
    public UserInfoController(ContextUserRetriever contextUserRetriever, UserDAO userDAO, UserFlowExecutor flowExecutor) {
        this.contextUserRetriever = contextUserRetriever;
        this.flowExecutor = flowExecutor;
        this.userDAO = userDAO;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public AppUser getCurrentUser() {
        return this.contextUserRetriever.getContextUser();
    }

    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    public AppUser changeProfileInfo(@RequestBody AppUser userToUpdate) throws Exception {
        userToUpdate.setId(this.contextUserRetriever.getContextUserId());
        return this.flowExecutor.changeProfileInfo(userToUpdate);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<AppUser> findPeople(String nameOrLogin, boolean searchByLogin) {
        QueryParams params = new QueryParams();
        params.setNameOrLogin(nameOrLogin);
        params.setSearchByLogin(searchByLogin);
        return this.userDAO.search(params);
    }

    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    public AppUser setProfilePicture(MultipartFile picture) throws Exception {
        AppUser user = this.getCurrentUser();
        user.setPicture(picture.getBytes());
        return this.changeProfileInfo(user);
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public AppUser changePassword(@RequestBody RawCredentials rawCredentials) throws Exception {
        return this.flowExecutor.changePassword(this.getCurrentUser(), rawCredentials);
    }
}