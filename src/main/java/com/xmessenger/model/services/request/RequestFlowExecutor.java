package com.xmessenger.model.services.request;

import com.xmessenger.model.database.entities.Request;
import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.request.dao.RequestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RequestFlowExecutor {
    private final RequestDAO requestDAO;

    @Autowired
    public RequestFlowExecutor(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    public List<Request> retrieveRequests(ApplicationUser user) {
        return this.requestDAO.retrieveRequestsForUser(user);
    }

    public Request createRequest(ApplicationUser sender, ApplicationUser recipient) throws RequestFlowException {
        Request request = new Request(sender, recipient);
        if (sender.getId().equals(recipient.getId())) {
            throw new RequestFlowException("You cannot send friendship request to yourself.");
        }
        if (this.requestDAO.requestExists(request)) {
            throw new RequestFlowException("The request has already been sent.");
        }
        request.setCreatedDate(new Date());
        return this.requestDAO.createRequest(request);
    }

    public Request processRequest(Request requestToProcess, ApplicationUser intendedRecipient) throws Exception {
        Request foundRequest = this.requestDAO.lookupRequest(requestToProcess);
        if (foundRequest == null) {
            throw new RequestFlowException("Friendship request cannot be found.");
        } else if (!intendedRecipient.equals(foundRequest.getRecipient())) {
            throw new RequestFlowException("Request recipient didn't pass validity check.");
        }
        foundRequest.setApproved(requestToProcess.getApproved());
        this.requestDAO.deleteRequest(foundRequest);
        return foundRequest;
    }

    public static class RequestFlowException extends Exception {
        public RequestFlowException(String message) {
            super(message);
        }
    }
}
