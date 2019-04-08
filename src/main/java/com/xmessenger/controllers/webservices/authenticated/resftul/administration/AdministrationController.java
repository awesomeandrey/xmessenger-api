package com.xmessenger.controllers.webservices.authenticated.resftul.administration;

import com.xmessenger.configs.WebSecurityConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/admin")
public class AdministrationController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getRequests() {
        return "Available only for ADMINs";
    }
}
