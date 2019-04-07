package com.xmessenger.controllers.webservices.authenticated.resftul.chatter.core;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.ContextUserRetriever;
import com.xmessenger.model.services.chatter.ChatterFlowExecutor;
import com.xmessenger.model.services.chatter.decorators.Chat;
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
    private final ContextUserRetriever contextUserRetriever;
    private final ApplicationEventPublisher publisher;
    private final ChatterFlowExecutor flowExecutor;

    @Autowired
    public ChattingController(ContextUserRetriever contextUserRetriever, ApplicationEventPublisher publisher, ChatterFlowExecutor flowExecutor) {
        this.contextUserRetriever = contextUserRetriever;
        this.publisher = publisher;
        this.flowExecutor = flowExecutor;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Chat> retrieveChats() {
        AppUser user = this.contextUserRetriever.getContextUser();
        return new ArrayList<>(this.flowExecutor.retrieveChats(user).values());
    }

    @RequestMapping(value = "/{chatId}/messages", method = RequestMethod.GET)
    public List<Message> retrieveMessages(@PathVariable Integer chatId) {
        return this.flowExecutor.retrieveMessages(new Chat(chatId));
    }

    @RequestMapping(value = "/{chatId}/messages", method = RequestMethod.POST)
    public Message sendMessage(@PathVariable("chatId") Integer chatId, @RequestBody Message message) throws Exception {
        message.getRelation().setId(chatId);
        message.setAuthor(this.contextUserRetriever.getContextUser());
        Message postedMessage = this.flowExecutor.postMessage(message);
        this.publisher.publishEvent(new AfterCreateEvent(postedMessage));
        return postedMessage;
    }

    @RequestMapping(value = "/{chatId}/clear", method = RequestMethod.DELETE)
    public void clearChat(@PathVariable("chatId") Integer chatId) {
        AppUser user = this.contextUserRetriever.getContextUser();
        Chat clearedChat = this.flowExecutor.clearChat(user, new Chat(chatId));
        clearedChat.setUpdatedBy(user);
        this.publisher.publishEvent(new ChatClearEvent(this, clearedChat));
    }

    @RequestMapping(value = "/{chatId}/delete", method = RequestMethod.DELETE)
    public void deleteChat(@PathVariable("chatId") Integer chatId) {
        AppUser user = this.contextUserRetriever.getContextUser();
        Chat deletedChat = this.flowExecutor.deleteChat(user, new Chat(chatId));
        deletedChat.setUpdatedBy(user);
        this.publisher.publishEvent(new ChatDeleteEvent(this, deletedChat));
    }
}