package com.xmessenger.controllers.webservices.config;

import com.google.gson.Gson;
import com.xmessenger.controllers.webservices.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getLocalizedMessage().split(":")[1].trim());
        response.put("locale", request.getLocale());
        response.put("details", request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
