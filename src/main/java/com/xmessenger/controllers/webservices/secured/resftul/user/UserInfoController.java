package com.xmessenger.controllers.webservices.secured.resftul.user;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.database.entities.core.Indicator;
import com.xmessenger.model.database.entities.enums.Role;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.async.AsynchronousService;
import com.xmessenger.model.services.core.chatter.RelationService;
import com.xmessenger.model.services.core.user.indicators.IndicatorService;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.dao.decorators.QueryParams;
import com.xmessenger.model.services.core.user.credentials.decorators.RawCredentials;
import com.xmessenger.model.services.core.user.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/user")
public class UserInfoController {
    private final ContextUserHolder contextUserHolder;
    private final UserService userService;
    private final RelationService relationService;
    private final UserValidator userValidator;
    private final IndicatorService indicatorService;
    private final AsynchronousService asynchronousService;

    @Autowired
    public UserInfoController(ContextUserHolder contextUserHolder, UserService userService, RelationService relationService, UserValidator userValidator, IndicatorService indicatorService, AsynchronousService asynchronousService) {
        this.contextUserHolder = contextUserHolder;
        this.userService = userService;
        this.relationService = relationService;
        this.userValidator = userValidator;
        this.indicatorService = indicatorService;
        this.asynchronousService = asynchronousService;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public AppUser getCurrentUser() {
        return this.contextUserHolder.getContextUser();
    }

    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    public AppUser changeProfileInfo(@RequestBody AppUser userToUpdate) {
        userToUpdate.setId(this.contextUserHolder.getContextUserId());
        return this.userService.changeProfileInfo(userToUpdate);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<AppUser> findPeople(String nameOrLogin, boolean searchByLogin) {
        QueryParams params = new QueryParams();
        params.setNameOrLogin(nameOrLogin);
        params.setSearchByLogin(searchByLogin);
        return this.userService.search(params).stream()
                .filter(appUser -> !appUser.getRoles().contains(Role.ROLE_ADMIN)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/indicators", method = RequestMethod.GET)
    public List<Indicator> getFellowsIndicators() {
        Map<Integer, AppUser> fellowsMap = this.relationService.getRelatedUsersMap(this.getCurrentUser());
        return this.indicatorService.getIndicators(fellowsMap);
    }

    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    public AppUser setProfilePicture(@RequestParam("file") MultipartFile picture) throws Exception {
        AppUser user = this.getCurrentUser();
        user.setPicture(picture.getBytes());
        return this.changeProfileInfo(user);
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public AppUser changePassword(@RequestBody RawCredentials rawCredentials) {
        AppUser runningUser = this.getCurrentUser();
        UserValidator.Result validationResult = this.userValidator.validateOnPasswordChange(runningUser, rawCredentials);
        if (validationResult.isValid()) {
            return this.userService.changePassword(runningUser, rawCredentials.getNewPassword());
        } else {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout() {
        AppUser appUser = this.getCurrentUser();
        this.asynchronousService.switchAppUserIndicator(appUser, false);
    }
}