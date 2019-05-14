package com.xmessenger.model.database.repositories.core;

import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RelationRepository extends CrudRepository<Relation, Integer> {
    @Query(value = "select relation from Relation relation " +
            "where (relation.userOne.id = :id1 and relation.userTwo.id = :id2)" +
            "or (relation.userOne.id = :id2 and relation.userTwo.id = :id1)")
    Relation getRelationByUserReferences(@Param("id1") Integer id1, @Param("id2") Integer id2);

    List<Relation> findAllByUserOneOrUserTwo(AppUser sameUser1, AppUser sameUser2);
}