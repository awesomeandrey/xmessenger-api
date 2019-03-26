package com.xmessenger.model.database.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "`message`")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mid")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "author")
    private ApplicationUser author;

    @ManyToOne
    @JoinColumn(name = "relation")
    private Relation relation;

    @Column(name = "body")
    private String body;

    @Column(name = "date")
    private Date date;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer recordId) {
        this.id = recordId;
    }

    public ApplicationUser getAuthor() {
        return this.author;
    }

    public void setAuthor(ApplicationUser author) {
        this.author = author;
    }

    public Relation getRelation() {
        return this.relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Message() {
        this.relation = new Relation();
    }

    public Message(ApplicationUser author) {
        this();
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(author, message.author) &&
                Objects.equals(relation, message.relation) &&
                Objects.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, relation, body, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", author=" + author +
                ", relation=" + relation +
                ", body='" + body + '\'' +
                ", date=" + date +
                '}';
    }
}