package com.xmessenger.model.database.repositories.core;

import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface UserRepository extends CrudRepository<AppUser, Integer> {

    AppUser findByUsername(String username);

    List<AppUser> findTop5ByNameContainingAndActiveTrue(String name);

    List<AppUser> findTop5ByUsernameContainingAndActiveTrue(String username);
}