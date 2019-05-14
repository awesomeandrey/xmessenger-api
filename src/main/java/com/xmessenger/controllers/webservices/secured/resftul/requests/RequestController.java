package com.xmessenger.controllers.webservices.secured.resftul.requests;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.user.details.ContextUserRetriever;
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
    private final ContextUserRetriever contextUserRetriever;
    private final ApplicationEventPublisher publisher;
    private final RequestService requestService;
    private final ChattingService chattingService;

    @Autowired
    public RequestController(ContextUserRetriever contextUserRetriever, ApplicationEventPublisher publisher, RequestService requestService, ChattingService chattingService) {
        this.contextUserRetriever = contextUserRetriever;
        this.publisher = publisher;
        this.requestService = requestService;
        this.chattingService = chattingService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Request> getRequests() {
        AppUser user = this.contextUserRetriever.getContextUser();
        return this.requestService.retrieveRequests(user);
    }

    @RequestMapping(value = "/process", method = RequestMethod.PUT)
    public Request processRequest(@RequestBody Request requestToProcess) throws Exception {
        AppUser user = this.contextUserRetriever.getContextUser();
        Request processedRequest = this.requestService.processRequest(requestToProcess, user);
        if (processedRequest.getApproved()) {
            this.chattingService.createChat(processedRequest.getSender(), processedRequest.getRecipient());
        }
        this.publisher.publishEvent(new AfterDeleteEvent(processedRequest));
        return processedRequest;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Request sendRequest(@RequestBody Request requestToCreate) throws Exception {
        AppUser recipient = requestToCreate.getRecipient(), sender = this.contextUserRetriever.getContextUser();
        if (this.chattingService.isFellow(sender, recipient)) {
            throw new RequestService.RequestFlowException("The request recipient is already your friend.");
        }
        Request request = this.requestService.createRequest(sender, recipient);
        this.publisher.publishEvent(new AfterCreateEvent(request));
        return request;
    }
}