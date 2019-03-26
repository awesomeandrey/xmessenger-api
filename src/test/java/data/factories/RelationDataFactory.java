package data.factories;

import com.xmessenger.model.database.entities.Relation;
import com.xmessenger.model.database.entities.ApplicationUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationDataFactory {
    private static final int TEST_RID = 777;

    public static Relation generateTestRelation(ApplicationUser u1, ApplicationUser u2) {
        Relation testRelation = new Relation(u1, u2);
        testRelation.setId(TEST_RID);
        return testRelation;
    }

    public static List<Relation> generateRelationsForUser(ApplicationUser user, Map<Integer, ApplicationUser> userToRelate) {
        List<Relation> relations = new ArrayList<>();
        userToRelate.values().forEach(testUser -> {
            Relation rel = new Relation(user, testUser);
            rel.setId(user.getId() + testUser.getId() + 1);
            relations.add(rel);
        });
        return relations;
    }

    public static Map<Integer, Relation> generateRelationsMapForUser(ApplicationUser user, Map<Integer, ApplicationUser> userToRelate) {
        Map<Integer, Relation> relationMap = new HashMap<>();
        generateRelationsForUser(user, userToRelate).forEach(relation -> {
            relationMap.put(relation.getId(), relation);
        });
        return relationMap;
    }
}
