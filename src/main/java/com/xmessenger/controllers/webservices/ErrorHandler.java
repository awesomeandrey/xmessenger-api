package com.xmessenger.controllers.webservices;

import com.xmessenger.configs.WebSecurityConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/test")
public class ErrorHandler {

    @RequestMapping("/api-error")
    public String name(Throwable e) {
        System.out.println(">>>" + e.toString());
        e.printStackTrace();
        return "api error";
    }
}
