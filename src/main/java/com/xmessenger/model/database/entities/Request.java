package com.xmessenger.model.database.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "`request`")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "frid")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender")
    private ApplicationUser sender;

    @ManyToOne
    @JoinColumn(name = "recipient")
    private ApplicationUser recipient;

    @Column(name = "is_approved")
    private Boolean approved;

    @Column(name = "created_date")
    private Date createdDate;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer recordId) {
        this.id = recordId;
    }

    public ApplicationUser getSender() {
        return this.sender;
    }

    public void setSender(ApplicationUser sender) {
        this.sender = sender;
    }

    public ApplicationUser getRecipient() {
        return this.recipient;
    }

    public void setRecipient(ApplicationUser recipient) {
        this.recipient = recipient;
    }

    public Boolean getApproved() {
        return this.approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Request() {
        this.approved = false;
    }

    public Request(ApplicationUser sender, ApplicationUser recipient) {
        this();
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(sender, request.sender) &&
                Objects.equals(recipient, request.recipient) &&
                Objects.equals(approved, request.approved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, recipient, approved, createdDate);
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", getApproved=" + approved +
                '}';
    }
}
