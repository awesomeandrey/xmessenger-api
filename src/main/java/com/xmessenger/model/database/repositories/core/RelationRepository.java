package com.xmessenger.model.database.repositories.core;

import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface RelationRepository extends PagingAndSortingRepository<Relation, Integer> {
    @Query(value = "from Relation relation " +
            "where (relation.userOne = ?1 and relation.userTwo = ?2)" +
            "or (relation.userOne = ?2 and relation.userTwo = ?1)")
    Relation getRelationByUserReferences(AppUser appUser1, AppUser appUser2);

    List<Relation> findAllByUserOneOrUserTwo(AppUser sameUser1, AppUser sameUser2);

    @Query(value = "select relation, (select max(date) from Message message where message.relation = relation) as last_message_date " +
            "from Relation relation where relation in (select relation from Relation relation where relation.userOne = ?1 or relation.userTwo = ?1) " +
            "order by last_message_date asc", countQuery = "from Relation relation where relation.userOne = ?1 or relation.userTwo = ?1")
    Page<Object[]> aggregateUserRelationsByLastMessageDate(AppUser appUser, Pageable pageable);
}