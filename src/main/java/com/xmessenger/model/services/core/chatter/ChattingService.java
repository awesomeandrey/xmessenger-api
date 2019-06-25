package com.xmessenger.model.services.core.chatter;

import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.database.entities.decorators.Chat;
import com.xmessenger.model.services.core.chatter.core.MessageService;
import com.xmessenger.model.services.core.chatter.core.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ChattingService {
    private final RelationService relationService;
    private final MessageService messageService;

    @Autowired
    public ChattingService(RelationService relationService, MessageService messageService) {
        this.relationService = relationService;
        this.messageService = messageService;
    }

    public boolean hasAuthorityToOperateWithChat(AppUser authorizedUser, Chat chat) {
        Relation relation = this.relationService.lookupRelation(chat.getId());
        return relation != null && relation.isUserRelated(authorizedUser);
    }

    public Chat createChat(AppUser user1, AppUser user2) throws Exception {
        Relation relation = this.relationService.createRelation(user1, user2);
        return new Chat(relation);
    }

    public Chat clearChat(AppUser runningUser, Chat chatToClear) {
        this.messageService.deleteMessagesByRelation(chatToClear.getRelation());
        return this.retrieveChat(runningUser, chatToClear.getId());
    }

    public Chat deleteChat(AppUser runningUser, Chat chatToDelete) {
        chatToDelete = this.clearChat(runningUser, chatToDelete);
        this.relationService.deleteRelation(chatToDelete.getRelation());
        return chatToDelete;
    }

    public void deleteChatsAll(AppUser appUser) {
        Map<Integer, Relation> relationsMap = this.relationService.getUserRelations(appUser);
        List<Relation> relations = new ArrayList<>(relationsMap.values());
        this.messageService.deleteMessagesByRelations(relations);
        this.relationService.deleteRelations(relations);
    }

    public Map<Integer, Chat> retrieveChats(AppUser runningUser) {
        Map<Integer, Relation> relationsMap = this.relationService.getUserRelations(runningUser);
        Map<Integer, Date> latestMessageDateByRelation = this.messageService.groupLastMessageDateByRelations(relationsMap.values());
        Map<Integer, Chat> chatsMap = new HashMap<>();
        relationsMap.values().forEach((relation) -> {
            Chat chatEntity = new Chat(relation);
            chatEntity.setFellow(this.relationService.getFellowFromRelation(runningUser, relation));
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

    public boolean isFellow(AppUser user1, AppUser user2) {
        return this.relationService.hasRelation(user1, user2);
    }

    //******************************************************************************************************************

    private Chat retrieveChat(AppUser runningUser, Integer chatId) {
        Relation relation = this.relationService.lookupRelation(chatId);
        Chat chat = new Chat(relation);
        chat.setFellow(this.relationService.getFellowFromRelation(runningUser, relation));
        return chat;
    }
}
