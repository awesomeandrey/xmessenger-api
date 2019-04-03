package com.xmessenger.model.services.chatter.decorators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xmessenger.model.database.entities.Relation;
import com.xmessenger.model.database.entities.ApplicationUser;

import java.util.Date;

public class Chat {
    @JsonIgnore
    private Relation relation;
    private ApplicationUser fellow;
    private Date latestMessageDate;
    private ApplicationUser updatedBy;

    public Integer getId() {
        return this.relation.getId();
    }

    public void setId(Integer id) {
        if (this.relation == null) {
            this.relation = new Relation();
        }
        this.relation.setId(id);
    }

    public ApplicationUser getFellow() {
        return this.fellow;
    }

    public void setFellow(ApplicationUser fellow) {
        this.fellow = fellow;
    }

    public ApplicationUser getStartedBy() {
        return this.relation.getUserOne();
    }

    public Date getLatestMessageDate() {
        return this.latestMessageDate;
    }

    public void setLatestMessageDate(Date latestMessageDate) {
        this.latestMessageDate = latestMessageDate;
    }

    public Relation getRelation() {
        return this.relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public ApplicationUser getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(ApplicationUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Chat() {
    }

    public Chat(Integer relationId) {
        this.setId(relationId);
    }

    public Chat(Relation relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "relation=" + relation +
                ", fellow=" + fellow +
                ", latestMessageDate=" + latestMessageDate +
                ", updatedBy=" + updatedBy +
                '}';
    }
}
