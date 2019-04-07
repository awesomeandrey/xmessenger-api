package com.xmessenger.model.database.repositories;

import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.Relation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findTop20ByRelationOrderByDateDesc(Relation relation);

    void deleteMessagesByRelation(Relation relation);

    @Query(value = "SELECT message.relation.id, MAX(message.date) " +
            "FROM Message message " +
            "WHERE message.relation IN :relations " +
            "GROUP BY message.relation.id")
    List<Object[]> aggregateMessagesDateByRelations(@Param("relations") Collection<Relation> relations);
}