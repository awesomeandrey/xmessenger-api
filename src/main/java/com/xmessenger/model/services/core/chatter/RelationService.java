package com.xmessenger.model.services.core.chatter;

import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.core.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelationService {
    private final RelationRepository relationRepository;

    @Autowired
    public RelationService(RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    public Map<Integer, Relation> getUserRelationsMap(AppUser user) {
        Map<Integer, Relation> relationsMap = new HashMap<>();
        this.relationRepository.findAllByUserOneOrUserTwo(user, user).forEach((Relation rel) -> {
            relationsMap.put(rel.getId(), rel);
        });
        return relationsMap;
    }

    public Map<Integer, AppUser> getRelatedUsersMap(AppUser user) {
        Map<Integer, AppUser> fellowsMap = new HashMap<>();
        this.getUserRelationsMap(user).values().forEach(relation -> {
            AppUser fellow = this.getFellowFromRelation(user, relation);
            fellowsMap.put(fellow.getId(), fellow);
        });
        return fellowsMap;
    }

    public List<Object[]> aggregateUserRelationsByLastMessageDate(AppUser appUser, Pageable pageable) {
        // Potentially, crop query applying 'OFFSET' + 'LIMIT' techniques;
        return this.relationRepository.aggregateUserRelationsByLastMessageDate(appUser, pageable);
    }

    public AppUser getFellowFromRelation(AppUser runningUser, Relation relation) {
        AppUser user1 = relation.getUserOne(), user2 = relation.getUserTwo();
        return user1.getId().equals(runningUser.getId()) ? user2 : user1;
    }

    public Relation lookupRelation(Integer relationId) {
        if (relationId == null) return null;
        return this.relationRepository.findOne(relationId);
    }

    public Relation createRelation(AppUser user1, AppUser user2) {
        Relation relation = new Relation(user1, user2);
        if (!this.isValid(relation)) {
            throw new IllegalArgumentException("Relation entity isn't valid.");
        }
        if (this.hasRelation(user1, user2)) {
            throw new IllegalArgumentException("The relation already exists.");
        }
        return this.relationRepository.save(relation);
    }

    public Long countUserRelations(AppUser runningUser) {
        return this.relationRepository.countByUserOneOrUserTwo(runningUser, runningUser);
    }

    public boolean hasRelation(AppUser user1, AppUser user2) {
        return this.relationRepository.getRelationByUserReferences(user1, user2) != null;
    }

    public void deleteRelation(Relation relation) {
        List<Relation> relationsToDelete = new ArrayList<>();
        relationsToDelete.add(relation);
        this.deleteRelations(relationsToDelete);
    }

    public void deleteRelations(List<Relation> relationsToDelete) {
        this.relationRepository.delete(relationsToDelete);
    }

    //******************************************************************************************************************

    private boolean isValid(Relation relation) {
        if (relation.getUserOne() == null || relation.getUserTwo() == null) return false;
        Integer uid1 = relation.getUserOne().getId(), uid2 = relation.getUserTwo().getId();
        if (uid1 == null || uid2 == null) return false;
        return !uid1.equals(uid2);
    }
}
