package data.factories;

import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.user.security.RawCredentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataFactory {
    private static int FELLOWS_AMOUNT = 5;
    public static int SUCCESS_USER_INDEX = 0;
    public static int FAILURE_USER_INDEX = 1;
    public static int DEFAULT_USER_INDEX = 2;

    public static ApplicationUser generateSuccessUser() {
        ApplicationUser testUser = generateTestUser();
        testUser.setId(1111);
        testUser.setName(testUser.getName().concat(" Success"));
        testUser.setUsername("tUser_success");
        return testUser;
    }

    public static ApplicationUser generateFailureUser() {
        ApplicationUser testUser = generateTestUser();
        testUser.setId(2222);
        testUser.setName(testUser.getName().concat(" Failure"));
        testUser.setUsername("tUser_failure");
        return testUser;
    }

    public static List<ApplicationUser> generateTestUsers() {
        List<ApplicationUser> users = new ArrayList<>();
        users.add(generateSuccessUser());
        users.add(generateFailureUser());
        users.add(generateTestUser());
        return users;
    }

    public static <Entity> List<Entity> castToCollection(Entity entity) {
        if (entity == null) {
            return new ArrayList<Entity>();
        }
        List<Entity> userList = new ArrayList<>();
        userList.add(entity);
        return userList;
    }

    public static Map<Integer, ApplicationUser> generateTestUsersMap() {
        Map<Integer, ApplicationUser> userMap = new HashMap<>();
        for (int i = 0; i < FELLOWS_AMOUNT; i++) {
            ApplicationUser testUser = generateTestUser();
            testUser.setId(testUser.getId() + i + 1);
            testUser.setUsername(testUser.getUsername().concat("#").concat(String.valueOf(i + 1)));
            userMap.put(testUser.getId(), testUser);
        }
        return userMap;
    }

    public static RawCredentials composeRawCredentials(ApplicationUser user) {
        RawCredentials rawCredentials = new RawCredentials();
        rawCredentials.setUsername(user.getUsername());
        rawCredentials.setPassword(user.getPassword());
        return rawCredentials;
    }

    private static ApplicationUser generateTestUser() {
        ApplicationUser testUser = new ApplicationUser();
        testUser.setId(333);
        testUser.setName("Test User");
        testUser.setUsername("tUser");
        testUser.setPassword("qwerty123456");
        testUser.setPicture(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        testUser.setActive(true);
        return testUser;
    }
}
