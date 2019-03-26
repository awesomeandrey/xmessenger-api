package data.factories;

import com.xmessenger.model.database.entities.Message;
import com.xmessenger.model.database.entities.Relation;
import com.xmessenger.model.database.entities.ApplicationUser;

public class MessageDataFactory {
    public final static String MESSAGE_BODY = "Test Message xMessenger";

    public static Message generateMessage(ApplicationUser author, Relation relation) {
        Message testMessage = new Message(author);
        testMessage.setId(relation.getId() + author.getId());
        testMessage.setRelation(relation);
        testMessage.setBody(MESSAGE_BODY);
        return testMessage;
    }
}