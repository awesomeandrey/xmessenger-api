package com.xmessenger.controllers.webservices.open.websockets.handlers;

import com.xmessenger.model.services.core.chatter.decorators.Chat;
import com.xmessenger.controllers.webservices.open.websockets.events.ChatClearEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.xmessenger.controllers.webservices.open.websockets.config.WebSocketConfig.TOPICS_PREFIX;

@Component
public class ChatClearHandler implements ApplicationListener<ChatClearEvent> {
    private final SimpMessagingTemplate websocket;

    @Autowired
    public ChatClearHandler(SimpMessagingTemplate websocket) {
        this.websocket = websocket;
    }

    @Override
    public void onApplicationEvent(ChatClearEvent chatClearEvent) {
        Chat chat = chatClearEvent.getChat();
        this.websocket.convertAndSend(
                TOPICS_PREFIX + "/chats/clear", chat
        );
    }
}
