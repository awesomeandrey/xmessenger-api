package com.xmessenger.model.database.entities.wrappers;

import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.Date;

@RedisHash("indicators")
public class Indicator {
    @Id
    private Integer id;
    private AppUser appUser;
    private Date timeStamp;

    public Integer getId() {
        return id;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Indicator(AppUser appUser) {
        this.appUser = appUser;
        this.id = appUser.getId();
        this.timeStamp = new Date();
    }
}
