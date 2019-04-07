package data.factories;

import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationDataFactory {
    private static final int TEST_RID = 777;

    public static Relation generateTestRelation(AppUser u1, AppUser u2) {
        Relation testRelation = new Relation(u1, u2);
        testRelation.setId(TEST_RID);
        return testRelation;
    }

    public static List<Relation> generateRelationsForUser(AppUser user, Map<Integer, AppUser> userToRelate) {
        List<Relation> relations = new ArrayList<>();
        userToRelate.values().forEach(testUser -> {
            Relation rel = new Relation(user, testUser);
            rel.setId(user.getId() + testUser.getId() + 1);
            relations.add(rel);
        });
        return relations;
    }

    public static Map<Integer, Relation> generateRelationsMapForUser(AppUser user, Map<Integer, AppUser> userToRelate) {
        Map<Integer, Relation> relationMap = new HashMap<>();
        generateRelationsForUser(user, userToRelate).forEach(relation -> {
            relationMap.put(relation.getId(), relation);
        });
        return relationMap;
    }
}
