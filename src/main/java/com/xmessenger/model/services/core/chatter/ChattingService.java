package com.xmessenger.model.services.core.chatter;

import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.services.core.chatter.decorators.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        if (relation == null) return false;
        if (relation.getUserOne().equals(authorizedUser)) return true;
        if (relation.getUserTwo().equals(authorizedUser)) return true;
        return false;
    }

    public Chat createChat(AppUser user1, AppUser user2) {
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
        Map<Integer, Relation> relationsMap = this.relationService.getUserRelationsMap(appUser);
        List<Relation> relations = new ArrayList<>(relationsMap.values());
        this.messageService.deleteMessagesByRelations(relations);
        this.relationService.deleteRelations(relations);
    }

    public Page<Chat> retrieveChats(AppUser runningUser, Pageable pageable) {
        List<Chat> userChats = new ArrayList<>();
        this.relationService.aggregateUserRelationsByLastMessageDate(runningUser, pageable).forEach(objects -> {
            Relation relation = (Relation) objects[0];
            Chat chatItem = new Chat(relation);
            chatItem.setFellow(this.relationService.getFellowFromRelation(runningUser, relation));
            chatItem.setLatestMessageDate((Date) objects[1]);
            userChats.add(chatItem);
        });
        return new PageImpl(userChats);
    }

    public Message postMessage(Message message) {
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
