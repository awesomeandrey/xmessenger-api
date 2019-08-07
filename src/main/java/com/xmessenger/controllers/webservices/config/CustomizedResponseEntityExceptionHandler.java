package com.xmessenger.controllers.webservices.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public final Map<String, Object> handleBadRequestException(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpServletResponse.SC_BAD_REQUEST);
        response.put("message", this.getExceptionMessage(ex));
        response.put("details", request.getDescription(false));
        return response;
    }

    private String getExceptionMessage(Exception ex) {
        String exceptionMessage = ex.getMessage(), splitCharacter = "Exception:";
        if (exceptionMessage.contains(splitCharacter)) {
            // java.lang.IllegalArgumentException: Some message;
            return exceptionMessage.split(splitCharacter)[1].trim();
        }
        return exceptionMessage;
    }
}