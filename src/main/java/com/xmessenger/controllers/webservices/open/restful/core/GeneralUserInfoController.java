package com.xmessenger.controllers.webservices.open.restful.core;

import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.services.core.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@OpenResource
public class GeneralUserInfoController {
    private final UserDAO userDAO;

    private static byte[] DEFAULT_USER_PICTURE;

    static {
        File file = null;
        try {
            file = new ClassPathResource("/static/pictures/default-user-picture.png").getFile();
            DEFAULT_USER_PICTURE = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            DEFAULT_USER_PICTURE = "".getBytes();
        }
    }

    @Autowired
    public GeneralUserInfoController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @RequestMapping(value = "/user/{uid}/picture", method = RequestMethod.GET)
    public byte[] getUserPicture(@PathVariable("uid") Integer uid, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "max-age=14400"); // 4 hours;
        byte[] picture = this.userDAO.getPicture(uid);
        if (picture == null) return DEFAULT_USER_PICTURE;
        return picture;
    }
}
