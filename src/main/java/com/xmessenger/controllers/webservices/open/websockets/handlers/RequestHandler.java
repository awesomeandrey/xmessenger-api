package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.model.database.entities.core.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.TOPICS_PREFIX;

@Component
@RepositoryEventHandler(Request.class)
public class RequestHandler {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public RequestHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @HandleAfterCreate
    public void sendRequest(Request request) {
        this.websocket.convertAndSend(
                TOPICS_PREFIX + "/requests/send", request
        );
    }

    @HandleAfterDelete
    public void processRequest(Request request) {
        this.websocket.convertAndSend(
                TOPICS_PREFIX + "/requests/process", request
        );
    }
}