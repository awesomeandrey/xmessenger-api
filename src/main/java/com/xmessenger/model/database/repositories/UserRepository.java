package com.xmessenger.model.database.repositories;

import com.xmessenger.model.database.entities.ApplicationUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface UserRepository extends CrudRepository<ApplicationUser, Integer> {

    ApplicationUser findByUsername(String username);

    List<ApplicationUser> findTop5ByNameContainingAndActiveTrue(String name);

    List<ApplicationUser> findTop5ByUsernameContainingAndActiveTrue(String username);
}