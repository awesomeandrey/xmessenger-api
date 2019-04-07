package data.factories;

import com.xmessenger.model.database.entities.core.Request;
import com.xmessenger.model.database.entities.core.AppUser;

import java.util.ArrayList;
import java.util.List;

public class RequestDataFactory {
    private static final Integer SUCCESS_RID = 777;
    private static final Integer FAILURE_RID = 333;

    public static List<Request> generateRequestsForUser(AppUser recipient) {
        List<Request> requests = new ArrayList<>();
        UserDataFactory.generateTestUsersMap().forEach((uid, sender) -> {
            requests.add(new Request(sender, recipient));
        });
        return requests;
    }

    public static Request createFailureRequest(AppUser sender, AppUser recipient) {
        Request testRequest = RequestDataFactory.createRequest(sender, recipient);
        testRequest.setId(RequestDataFactory.FAILURE_RID);
        return testRequest;
    }

    public static Request createSuccessRequest(AppUser sender, AppUser recipient) {
        Request testRequest = RequestDataFactory.createRequest(sender, recipient);
        testRequest.setId(RequestDataFactory.SUCCESS_RID);
        testRequest.setApproved(true);
        return testRequest;
    }

    private static Request createRequest(AppUser sender, AppUser recipient) {
        return new Request(sender, recipient);
    }
}
