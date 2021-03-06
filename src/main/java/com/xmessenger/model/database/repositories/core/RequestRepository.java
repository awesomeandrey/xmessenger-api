package com.xmessenger.model.database.repositories.core;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RequestRepository extends CrudRepository<Request, Integer> {

    Request findBySenderAndRecipient(AppUser sender, AppUser recipient);

    Request findByRecipientAndSender(AppUser recipient, AppUser sender);

    List<Request> findTop6ByRecipientAndApprovedIsFalseOrderByCreatedDate(AppUser user);

    List<Request> findByRecipientOrSender(AppUser user, AppUser sameUser);
}