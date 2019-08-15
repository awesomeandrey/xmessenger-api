package com.xmessenger.model.services.core.chatter;

import com.xmessenger.model.database.entities.core.Message;
import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.repositories.core.MessageRepository;
import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getMessagesByRelation(Relation relation) {
        return this.messageRepository.findTop20ByRelationOrderByDateDesc(relation);
    }

    public Message composeMessage(Message message) {
        if (!this.isValid(message)) {
            throw new IllegalArgumentException("Invalid message entity.");
        }
        message.setDate(new Date());
        return this.messageRepository.save(message);
    }

    public void deleteMessagesByRelation(Relation relation) {
        List<Relation> relationsToDelete = new ArrayList<>();
        relationsToDelete.add(relation);
        this.deleteMessagesByRelations(relationsToDelete);
    }

    public void deleteMessagesByRelations(List<Relation> relations) {
        this.messageRepository.deleteAllByRelationIn(relations);
    }

    private boolean isValid(Message message) {
        if (message.getAuthor() == null || message.getAuthor().getId() == null) return false;
        if (message.getRelation() == null || message.getRelation().getId() == null) return false;
        return !Utility.isBlank(message.getBody());
    }
}
