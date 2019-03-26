package com.xmessenger.controllers.routers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoutingController {
    @GetMapping(value = {"/", "/about"})
    public String toAbout() {
        return "about";
    }
}