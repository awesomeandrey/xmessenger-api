package com.xmessenger.model.database.entities.wrappers;

import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@RedisHash("indicators")
public class Indicator {
    @Id
    private Integer id;
    private boolean active;
    private String datestamp; // JSON encoded datestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public Indicator() {
        this.active = false;
        this.datestamp = new Date().toInstant().toString();
    }

    public Indicator(AppUser user) {
        this();
        this.id = user.getId();
    }

    public Indicator(AppUser user, boolean active) {
        this(user);
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Indicator indicator = (Indicator) o;
        return Objects.equals(id, indicator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Indicator change: " + this.id + " -> " + this.active + " -> " + this.datestamp;
    }
}
