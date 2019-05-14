package com.xmessenger.model.database.entities;

import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.Date;

@RedisHash("indicators")
public class AppUserIndicator {
    @Id
    private Integer id;
    private AppUser user;
    private boolean loggedIn;
    private Date dateStamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
        this.setId(user.getId());
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
        this.setUser(user);
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "User ID=" + id +
                ", loggedIn=" + loggedIn +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
