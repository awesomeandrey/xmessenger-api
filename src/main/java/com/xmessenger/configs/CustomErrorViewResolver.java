package com.xmessenger.configs;

import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class CustomErrorViewResolver implements ErrorViewResolver {
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        String pathStr = (String) model.get("path");
        System.out.println("PATH: " + pathStr);
        if (pathStr.contains("api")) {

            System.out.println("REST API error");

            return new ModelAndView("/errorpage/api");
        } else {

            System.out.println("To HTML page");

            return new ModelAndView("/error");
        }
    }
}
