package com.xmessenger.controllers.webservices.secured.resftul.users;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.database.entities.decorators.Indicator;
import com.xmessenger.model.database.entities.enums.Role;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.IndicatorService;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.dao.QueryParams;
import com.xmessenger.model.services.core.security.RawCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/user")
public class UserInfoController {
    private final ContextUserHolder contextUserHolder;
    private final UserService userService;
    private final IndicatorService indicatorService;

    @Autowired
    public UserInfoController(ContextUserHolder contextUserHolder, UserService userService, IndicatorService indicatorService) {
        this.contextUserHolder = contextUserHolder;
        this.userService = userService;
        this.indicatorService = indicatorService;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public AppUser getCurrentUser() {
        return this.contextUserHolder.getContextUser();
    }

    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    public AppUser changeProfileInfo(@Valid @RequestBody AppUser userToUpdate) throws Exception {
        userToUpdate.setId(this.contextUserHolder.getContextUserId());
        return this.userService.changeProfileInfo(userToUpdate);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<AppUser> findPeople(String nameOrLogin, boolean searchByLogin) {
        QueryParams params = new QueryParams();
        params.setNameOrLogin(nameOrLogin);
        params.setSearchByLogin(searchByLogin);
        return this.userService.lookupUsers(params).stream()
                .filter(appUser -> !appUser.getRoles().contains(Role.ROLE_ADMIN)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public Collection<Indicator> getFellowsIndicators() {
        Map<Integer, AppUser> fellowsMap = this.userService.findFellows(this.getCurrentUser());
        return this.indicatorService.getIndicators(fellowsMap);
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

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout() {
        AppUser appUser = this.getCurrentUser();
        this.indicatorService.switchIndicator(appUser, false);
    }
}