package com.xmessenger.model.services.core.request.dao;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.core.RequestRepository;
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
        if (Utility.isNotBlank(request.getId())) {
            return this.requestRepository.findOne(request.getId());
        }
        AppUser user1 = request.getSender(), user2 = request.getRecipient();
        Request foundRequest = this.requestRepository.findBySenderAndRecipient(user1, user2);
        if (foundRequest != null) return foundRequest;
        return this.requestRepository.findByRecipientAndSender(user1, user2);
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
