package com.xmessenger.model.database.repositories;

import com.xmessenger.model.database.entities.Request;
import com.xmessenger.model.database.entities.ApplicationUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RequestRepository extends CrudRepository<Request, Integer> {

    Request findBySenderAndRecipient(ApplicationUser sender, ApplicationUser recipient);

    List<Request> findByRecipientAndApprovedIsFalse(ApplicationUser user);
}