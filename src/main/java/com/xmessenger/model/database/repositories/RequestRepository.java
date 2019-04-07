package com.xmessenger.model.database.repositories;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RequestRepository extends CrudRepository<Request, Integer> {

    Request findBySenderAndRecipient(AppUser sender, AppUser recipient);

    List<Request> findByRecipientAndApprovedIsFalse(AppUser user);
}