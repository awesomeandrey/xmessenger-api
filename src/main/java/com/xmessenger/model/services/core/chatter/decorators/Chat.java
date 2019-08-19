package com.xmessenger.model.services.core.chatter.decorators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xmessenger.model.database.entities.core.Relation;
import com.xmessenger.model.database.entities.core.AppUser;

import java.util.Date;

public class Chat {
    private Integer chatId;
    private Date lastActivityDate; // Inert value (generated at runtime, non-persistent);
    private Date createdDate;
    private AppUser fellow;
    private AppUser lastUpdatedBy; // Inert value (generated at runtime, non-persistent);
    private AppUser startedBy;

    public Integer getChatId() {
        return this.chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Date getLastActivityDate() {
        return this.lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        if (lastActivityDate != null) {
            this.lastActivityDate = lastActivityDate;
        }
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public AppUser getFellow() {
        return this.fellow;
    }

    public void setFellow(AppUser fellow) {
        this.fellow = fellow;
    }

    public AppUser getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(AppUser lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public AppUser getStartedBy() {
        return this.startedBy;
    }

    public void setStartedBy(AppUser startedBy) {
        this.startedBy = startedBy;
    }

    @JsonIgnore
    public Relation getRelation() {
        Relation relation = new Relation();
        relation.setId(this.chatId);
        return relation;
    }

    public Chat() {
    }

    public Chat(Integer relationId) {
        this();
        this.chatId = relationId;
    }

    public Chat(Relation relation) {
        this(relation.getId());
        this.createdDate = relation.getCreatedDate();
        this.startedBy = relation.getUserOne();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", lastActivityDate=" + lastActivityDate +
                ", fellow=" + fellow.getId() +
                ", lastUpdatedBy=" + lastUpdatedBy.getId() +
                ", startedBy=" + startedBy.getId() +
                '}';
    }
}