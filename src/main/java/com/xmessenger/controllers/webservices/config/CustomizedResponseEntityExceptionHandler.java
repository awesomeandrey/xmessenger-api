package com.xmessenger.controllers.webservices.config;

import com.xmessenger.controllers.webservices.config.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public final Map<String, Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return this.composeResponseMap(ex, request);
    }

    private Map<String, Object> composeResponseMap(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getLocalizedMessage().split(":")[1].trim());
        response.put("locale", request.getLocale());
        response.put("details", request.getDescription(false));
        return response;
    }
}
