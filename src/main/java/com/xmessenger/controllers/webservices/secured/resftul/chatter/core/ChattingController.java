package com.xmessenger.controllers.webservices.secured.resftul.chatter.core;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.services.core.chatter.ChattingService;
import com.xmessenger.model.database.entities.decorators.Chat;
import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.controllers.webservices.open.websockets.events.ChatClearEvent;
import com.xmessenger.controllers.webservices.open.websockets.events.ChatDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/chats")
public class ChattingController {
    private final ContextUserHolder contextUserHolder;
    private final ApplicationEventPublisher publisher;
    private final ChattingService chattingService;

    @Autowired
    public ChattingController(ContextUserHolder contextUserHolder, ApplicationEventPublisher publisher, ChattingService chattingService) {
        this.contextUserHolder = contextUserHolder;
        this.publisher = publisher;
        this.chattingService = chattingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Chat> retrieveChats() {
        AppUser user = this.contextUserHolder.getContextUser();
        return new ArrayList<>(this.chattingService.retrieveChats(user).values());
    }

    @RequestMapping(value = "/{chatId}/messages", method = RequestMethod.GET)
    public List<Message> retrieveMessages(@PathVariable Integer chatId) {
        return this.chattingService.retrieveMessages(new Chat(chatId));
    }

    @RequestMapping(value = "/{chatId}/messages", method = RequestMethod.POST)
    public Message sendMessage(@PathVariable("chatId") Integer chatId, @RequestBody Message message) {
        message.getRelation().setId(chatId);
        message.setAuthor(this.contextUserHolder.getContextUser());
        Message postedMessage = this.chattingService.postMessage(message);
        this.publisher.publishEvent(new AfterCreateEvent(postedMessage));
        return postedMessage;
    }

    @RequestMapping(value = "/{chatId}/clear", method = RequestMethod.DELETE)
    public void clearChat(@PathVariable("chatId") Integer chatId) {
        AppUser user = this.contextUserHolder.getContextUser();
        Chat clearedChat = this.chattingService.clearChat(user, new Chat(chatId));
        clearedChat.setUpdatedBy(user);
        this.publisher.publishEvent(new ChatClearEvent(this, clearedChat));
    }

    @RequestMapping(value = "/{chatId}/delete", method = RequestMethod.DELETE)
    public void deleteChat(@PathVariable("chatId") Integer chatId) {
        AppUser user = this.contextUserHolder.getContextUser();
        Chat deletedChat = this.chattingService.deleteChat(user, new Chat(chatId));
        deletedChat.setUpdatedBy(user);
        this.publisher.publishEvent(new ChatDeleteEvent(this, deletedChat));
    }
}