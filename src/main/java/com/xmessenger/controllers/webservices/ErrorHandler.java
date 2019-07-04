package com.xmessenger.controllers.webservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandler {

    @RequestMapping("/errorpage/api")
    public String name(Throwable e) {
        System.out.println(">>>" + e.toString());
        e.printStackTrace();
        return "api error";
    }
}
