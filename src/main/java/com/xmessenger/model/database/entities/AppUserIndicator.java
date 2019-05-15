package com.xmessenger.model.database.entities;

import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@RedisHash("indicators")
public class AppUserIndicator {
    @Id
    private Integer id; // AppUser ID;
    private boolean loggedIn;
    private Date dateStamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }

    public AppUserIndicator() {
        this.loggedIn = false;
        this.dateStamp = new Date();
    }

    public AppUserIndicator(AppUser user) {
        this();
        this.id = user.getId();
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "User ID=" + id +
                ", loggedIn=" + loggedIn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserIndicator indicator = (AppUserIndicator) o;
        return Objects.equals(id, indicator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
