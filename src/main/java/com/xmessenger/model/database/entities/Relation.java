package com.xmessenger.model.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "`relation`")
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rid")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userone")
    @JsonIgnore
    private ApplicationUser userOne;

    @ManyToOne
    @JoinColumn(name = "usertwo")
    @JsonIgnore
    private ApplicationUser userTwo;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer recordId) {
        this.id = recordId;
    }

    public ApplicationUser getUserOne() {
        return this.userOne;
    }

    public void setUserOne(ApplicationUser userOne) {
        this.userOne = userOne;
    }

    public ApplicationUser getUserTwo() {
        return this.userTwo;
    }

    public void setUserTwo(ApplicationUser userTwo) {
        this.userTwo = userTwo;
    }

    public Relation() {
    }

    public Relation(ApplicationUser userOne, ApplicationUser userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
    }

    public boolean isUserRelated(ApplicationUser userToCheck) {
        if (this.userOne.equals(userToCheck)) return true;
        if (this.userTwo.equals(userToCheck)) return true;
        return false;
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