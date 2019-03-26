package com.xmessenger.controllers.webservices.open.restful.core;

import com.xmessenger.controllers.webservices.open.config.OpenResource;
import com.xmessenger.model.services.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@OpenResource
public class UserController {
    private final UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @RequestMapping(value = "/user/{uid}/picture", method = RequestMethod.GET)
    public byte[] getUserPicture(@PathVariable("uid") Integer uid, HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "max-age=14400"); // 4 hours;
        return this.userDAO.getPicture(uid);
    }
}
