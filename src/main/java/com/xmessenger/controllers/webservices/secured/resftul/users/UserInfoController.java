package com.xmessenger.controllers.webservices.secured.resftul.users;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserRetriever;
import com.xmessenger.model.database.entities.AppUserIndicator;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.IndicatorRepository;
import com.xmessenger.model.services.user.UserService;
import com.xmessenger.model.services.user.dao.QueryParams;
import com.xmessenger.model.services.user.security.RawCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/user")
public class UserInfoController {
    private final ContextUserRetriever contextUserRetriever;
    private final UserService userService;
    private final IndicatorRepository indicatorRepository;

    @Autowired
    public UserInfoController(ContextUserRetriever contextUserRetriever, UserService userService, IndicatorRepository indicatorRepository) {
        this.contextUserRetriever = contextUserRetriever;
        this.userService = userService;
        this.indicatorRepository = indicatorRepository;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public AppUser getCurrentUser() {
        return this.contextUserRetriever.getContextUser();
    }

    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    public AppUser changeProfileInfo(@RequestBody AppUser userToUpdate) throws Exception {
        userToUpdate.setId(this.contextUserRetriever.getContextUserId());
        return this.userService.changeProfileInfo(userToUpdate);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<AppUser> findPeople(String nameOrLogin, boolean searchByLogin) {
        QueryParams params = new QueryParams();
        params.setNameOrLogin(nameOrLogin);
        params.setSearchByLogin(searchByLogin);
        return this.userService.lookupUsers(params);
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public List<AppUserIndicator> getIndicators() {
        Map<Integer, AppUser> fellows = this.userService.findFellows(this.contextUserRetriever.getContextUser());
        return (List<AppUserIndicator>) this.indicatorRepository.findAll(fellows.keySet());
    }

    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    public AppUser setProfilePicture(@RequestParam("file") MultipartFile picture) throws Exception {
        AppUser user = this.getCurrentUser();
        user.setPicture(picture.getBytes());
        return this.changeProfileInfo(user);
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public AppUser changePassword(@RequestBody RawCredentials rawCredentials) throws Exception {
        return this.userService.changePassword(this.getCurrentUser(), rawCredentials);
    }
}