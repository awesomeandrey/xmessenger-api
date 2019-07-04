package com.xmessenger.controllers.webservices.secured.resftul.request;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserHolder;
import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.chatter.ChattingService;
import com.xmessenger.model.services.core.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(WebSecurityConfig.API_BASE_PATH + "/requests")
public class RequestController {
    private final ContextUserHolder contextUserHolder;
    private final ApplicationEventPublisher publisher;
    private final RequestService requestService;
    private final ChattingService chattingService;

    @Autowired
    public RequestController(ContextUserHolder contextUserHolder, ApplicationEventPublisher publisher, RequestService requestService, ChattingService chattingService) {
        this.contextUserHolder = contextUserHolder;
        this.publisher = publisher;
        this.requestService = requestService;
        this.chattingService = chattingService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Request> getRequests() {
        AppUser user = this.contextUserHolder.getContextUser();
        return this.requestService.retrieveRequests(user);
    }

    @RequestMapping(value = "/process", method = RequestMethod.PUT)
    public Request processRequest(@RequestBody Request requestToProcess) throws Exception {
        AppUser user = this.contextUserHolder.getContextUser();
        Request foundRequest = this.requestService.lookupRequest(requestToProcess);
        if (foundRequest == null) {
            throw new IllegalArgumentException("Friendship request was not found.");
        } else if (!user.equals(foundRequest.getRecipient())) {
            throw new IllegalArgumentException("Request recipient didn't pass validity check.");
        }
        if (requestToProcess.getApproved()) {
            this.chattingService.createChat(foundRequest.getSender(), foundRequest.getRecipient());
        }
        this.requestService.deleteRequest(foundRequest);
        this.publisher.publishEvent(new AfterDeleteEvent(foundRequest));
        return foundRequest;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Request sendRequest(@RequestBody Request requestToCreate) {
        AppUser recipient = requestToCreate.getRecipient(), sender = this.contextUserHolder.getContextUser();
        if (this.chattingService.isFellow(sender, recipient)) {
            throw new IllegalArgumentException("The request recipient is already your friend.");
        }
        Request request = this.requestService.createRequest(sender, recipient);
        this.publisher.publishEvent(new AfterCreateEvent(request));
        return request;
    }
}