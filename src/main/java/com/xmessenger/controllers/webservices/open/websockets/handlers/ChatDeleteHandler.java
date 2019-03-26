package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.model.services.chatter.decorators.Chat;
import com.xmessenger.controllers.webservices.open.websockets.events.ChatDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.MESSAGE_PREFIX;

@Component
public class ChatDeleteHandler implements ApplicationListener<ChatDeleteEvent> {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public ChatDeleteHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @Override
    public void onApplicationEvent(ChatDeleteEvent chatDeleteEvent) {
        Chat chat = chatDeleteEvent.getChat();
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/chat/delete", chat
        );
    }
}
