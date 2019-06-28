package com.xmessenger.model.services.core.request;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.core.RequestRepository;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Looks up request entity either by record ID,
     * or by secondary properties (recipient, sender).
     *
     * @param request - Shell copy of request entity.
     * @return Request entity with all properties filled.
     */
    public Request lookupRequest(Request request) {
        if (Utility.isNotBlank(request.getId())) {
            return this.requestRepository.findOne(request.getId());
        }
        AppUser user1 = request.getSender(), user2 = request.getRecipient();
        Request foundRequest = this.requestRepository.findBySenderAndRecipient(user1, user2);
        if (foundRequest != null) return foundRequest;
        return this.requestRepository.findByRecipientAndSender(user1, user2);
    }

    /**
     * Fetches all non-approved friendship request for
     * passed as a parameter application user.
     *
     * @param user - Application user whose request are to be retrieved.
     * @return List of friendship request.
     */
    public List<Request> retrieveRequests(AppUser user) {
        return this.requestRepository.findByRecipientAndApprovedIsFalse(user);
    }

    /**
     * Generates new friendship request based on input parameters.
     * Also, performs input validation.
     *
     * @param sender    - Application user who sends friendship request.
     * @param recipient - Intended receiver of friendship request.
     * @return Created request entity.
     */
    public Request createRequest(AppUser sender, AppUser recipient) {
        Request request = new Request(sender, recipient);
        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("You cannot send friendship request to yourself.");
        }
        if (this.requestExists(request)) {
            throw new IllegalArgumentException("The request has already been sent.");
        }
        request.setCreatedDate(new Date());
        return this.requestRepository.save(request);
    }

    /**
     * Deletes request record from database.
     *
     * @param request - Request entity to delete.
     */
    public void deleteRequest(Request request) {
        this.requestRepository.delete(request);
    }

    /**
     * Removes all user related request from database.
     *
     * @param appUser - Application user whose friendship request are to be deleted.
     */
    public void deleteRequestsAll(AppUser appUser) {
        List<Request> requests = this.requestRepository.findByRecipientOrSender(appUser, appUser);
        if (!requests.isEmpty()) {
            this.requestRepository.delete(requests);
        }
    }

    private boolean requestExists(Request request) {
        return this.lookupRequest(request) != null;
    }
}