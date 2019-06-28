package com.xmessenger.model.database.entities.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "xm_relation")
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relation_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_one_id")
    @JsonIgnore
    private AppUser userOne;

    @ManyToOne
    @JoinColumn(name = "user_two_id")
    @JsonIgnore
    private AppUser userTwo;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer recordId) {
        this.id = recordId;
    }

    public AppUser getUserOne() {
        return this.userOne;
    }

    public void setUserOne(AppUser userOne) {
        this.userOne = userOne;
    }

    public AppUser getUserTwo() {
        return this.userTwo;
    }

    public void setUserTwo(AppUser userTwo) {
        this.userTwo = userTwo;
    }

    public Relation() {
    }

    public Relation(AppUser userOne, AppUser userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", userOne=" + userOne +
                ", userTwo=" + userTwo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return Objects.equals(userOne, relation.userOne) &&
                Objects.equals(userTwo, relation.userTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userOne, userTwo);
    }
}