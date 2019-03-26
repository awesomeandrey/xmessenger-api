package com.xmessenger.model.services.chatter;

import com.xmessenger.model.database.entities.Message;
import com.xmessenger.model.database.entities.Relation;
import com.xmessenger.model.database.entities.ApplicationUser;
import com.xmessenger.model.services.chatter.decorators.Chat;
import com.xmessenger.model.services.chatter.core.MessageService;
import com.xmessenger.model.services.chatter.core.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ChatterFlowExecutor {
    private final RelationService relationService;
    private final MessageService messageService;

    @Autowired
    public ChatterFlowExecutor(RelationService relationService, MessageService messageService) {
        this.relationService = relationService;
        this.messageService = messageService;
    }

    public boolean hasAuthorityToOperateWithChat(ApplicationUser authorizedUser, Chat chat) {
        Relation relation = this.relationService.lookupRelation(chat.getId());
        return relation != null && relation.isUserRelated(authorizedUser);
    }

    public Chat createChat(ApplicationUser user1, ApplicationUser user2) throws Exception {
        Relation relation = this.relationService.createRelation(user1, user2);
        return new Chat(relation);
    }

    public Chat clearChat(ApplicationUser runningUser, Chat chatToClear) {
        this.messageService.deleteMessagesByRelation(chatToClear.getRelation());
        return this.retrieveChat(runningUser, chatToClear.getId());
    }

    public Chat deleteChat(ApplicationUser runningUser, Chat chatToDelete) {
        chatToDelete = this.clearChat(runningUser, chatToDelete);
        this.relationService.deleteRelation(chatToDelete.getRelation());
        return chatToDelete;
    }

    public Map<Integer, Chat> retrieveChats(ApplicationUser runningUser) {
        Map<Integer, Relation> relationsMap = this.relationService.getUserRelations(runningUser);
        Map<Integer, Date> latestMessageDateByRelation = this.messageService.groupLastMessageDateByRelations(relationsMap.values());
        Map<Integer, Chat> chatsMap = new HashMap<>();
        relationsMap.values().forEach((relation) -> {
            Chat chatEntity = new Chat(relation);
            chatEntity.setFellow(this.getFellowFromRelation(runningUser, relation));
            if (latestMessageDateByRelation.containsKey(relation.getId())) {
                chatEntity.setLatestMessageDate(latestMessageDateByRelation.get(relation.getId()));
            }
            chatsMap.put(chatEntity.getId(), chatEntity);
        });
        return chatsMap;
    }

    public Message postMessage(Message message) throws Exception {
        return this.messageService.composeMessage(message);
    }

    public List<Message> retrieveMessages(Chat chat) {
        Relation relation = chat.getRelation();
        return this.messageService.getMessagesByRelation(relation);
    }

    public boolean isFellow(ApplicationUser user1, ApplicationUser user2) {
        return this.relationService.hasRelation(user1, user2);
    }

    //******************************************************************************************************************

    private ApplicationUser getFellowFromRelation(ApplicationUser runningUser, Relation relation) {
        ApplicationUser user1 = relation.getUserOne(), user2 = relation.getUserTwo();
        return user1.getId().equals(runningUser.getId()) ? user2 : user1;
    }

    private Chat retrieveChat(ApplicationUser runningUser, Integer chatId) {
        Relation relation = this.relationService.lookupRelation(chatId);
        Chat chat = new Chat(relation);
        chat.setFellow(this.getFellowFromRelation(runningUser, relation));
        return chat;
    }
}
