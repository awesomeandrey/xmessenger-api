package com.xmessenger.controllers.webservices.open.websockets.events;

import com.xmessenger.model.services.chatter.decorators.Chat;
import org.springframework.context.ApplicationEvent;

public class ChatDeleteEvent extends ApplicationEvent {
    private Chat chat;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ChatDeleteEvent(Object source, Chat chat) {
        super(source);
        this.chat = chat;
    }

}