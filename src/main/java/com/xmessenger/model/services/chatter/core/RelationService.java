package com.xmessenger.model.services.chatter.core;

import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.repositories.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelationService {
    private final RelationRepository relationRepository;

    @Autowired
    public RelationService(RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    public Map<Integer, Relation> getUserRelations(AppUser user) {
        Map<Integer, Relation> relationsMap = new HashMap<>();
        this.relationRepository.findAllByUserOneOrUserTwo(user, user).forEach((Relation rel) -> {
            relationsMap.put(rel.getId(), rel);
        });
        return relationsMap;
    }

    public Relation lookupRelation(Integer relationId) {
        if (relationId == null) return null;
        return this.relationRepository.findOne(relationId);
    }

    public Relation createRelation(AppUser user1, AppUser user2) throws RelationException {
        Relation relation = new Relation(user1, user2);
        if (!this.isValid(relation)) {
            throw new RelationException("Relation entity isn't valid.");
        }
        if (this.hasRelation(user1, user2)) {
            throw new RelationException("The relation already exists.");
        }
        return this.relationRepository.save(relation);
    }

    public boolean hasRelation(AppUser user1, AppUser user2) {
        return this.relationRepository.getRelationByUserReferences(user1.getId(), user2.getId()) != null;
    }

    public void deleteRelation(Relation relation) {
        this.relationRepository.delete(relation);
    }

    public AppUser getFellowFromRelation(AppUser runningUser, Relation relation) {
        AppUser user1 = relation.getUserOne(), user2 = relation.getUserTwo();
        return user1.getId().equals(runningUser.getId()) ? user2 : user1;
    }

    //******************************************************************************************************************

    private boolean isValid(Relation relation) {
        if (relation.getUserOne() == null || relation.getUserTwo() == null) return false;
        Integer uid1 = relation.getUserOne().getId(), uid2 = relation.getUserTwo().getId();
        if (uid1 == null || uid2 == null) return false;
        return !uid1.equals(uid2);
    }

    public class RelationException extends Exception {
        public RelationException(String message) {
            super(message);
        }
    }
}
