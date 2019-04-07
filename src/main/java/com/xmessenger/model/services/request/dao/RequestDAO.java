package com.xmessenger.model.services.request.dao;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.RequestRepository;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestDAO {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestDAO(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> retrieveRequestsForUser(AppUser user) {
        return this.requestRepository.findByRecipientAndApprovedIsFalse(user);
    }

    public Request lookupRequest(Request request) {
        Request foundRequest = null;
        if (Utility.isNotBlank(request.getId())) {
            foundRequest = this.requestRepository.findOne(request.getId());
        }
        if (foundRequest == null) {
            AppUser sender = request.getSender(), recipient = request.getRecipient();
            foundRequest = this.requestRepository.findBySenderAndRecipient(sender, recipient);
        }
        return foundRequest;
    }

    public Request createRequest(Request request) {
        return this.requestRepository.save(request);
    }

    public boolean requestExists(Request request) {
        return this.lookupRequest(request) != null;
    }

    public void deleteRequest(Request request) {
        this.requestRepository.delete(request);
    }
}
