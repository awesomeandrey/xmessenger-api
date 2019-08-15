package com.xmessenger.model.database.repositories.core;

import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.Relation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findTop20ByRelationOrderByDateDesc(Relation relation);

    void deleteAllByRelationIn(List<Relation> relations);
}