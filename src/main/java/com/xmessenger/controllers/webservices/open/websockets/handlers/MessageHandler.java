package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.model.database.entities.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.TOPICS_PREFIX;

@Component
@RepositoryEventHandler(Message.class)
public class MessageHandler {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public MessageHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @HandleAfterCreate
    public void sendMessage(Message message) {
        this.websocket.convertAndSend(
                TOPICS_PREFIX + "/messages/send", message
        );
    }
}