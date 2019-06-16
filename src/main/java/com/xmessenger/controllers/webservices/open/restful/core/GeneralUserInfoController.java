package com.xmessenger.controllers.webservices.open.restful.core;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.user.UserService;
import com.xmessenger.model.services.core.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@OpenResource
public class GeneralUserInfoController {
    private final UserService userService;

    @Autowired
    public GeneralUserInfoController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/user/{uid}/picture", method = RequestMethod.GET)
    public byte[] getUserPicture(@PathVariable("uid") Integer uid, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "max-age=14400"); // 4 hours;
        // Create shell copy;
        AppUser appUser = new AppUser();
        appUser.setId(uid);
        // Query app user info;
        appUser = this.userService.lookupUser(appUser);
        if (appUser == null) throw new UserNotFoundException(uid);
        if (appUser.getPicture() == null) {
//            File file = new ClassPathResource("static/pictures/default-user-picture.png").getFile();
//            return Files.readAllBytes(file.toPath());

            return Resources.toByteArray(Resources.getResource("static/pictures/default-user-picture.png"));

        }
        return appUser.getPicture();
    }
}
