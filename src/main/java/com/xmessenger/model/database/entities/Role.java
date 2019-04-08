package com.xmessenger.model.database.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER(1), ROLE_ADMIN(2);

    private int code;

    private Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static Role fromCode(int code) {
        switch (code) {
            case 1:
                return Role.ROLE_USER;
            case 2:
                return Role.ROLE_ADMIN;
            default:
                return Role.ROLE_USER;
        }
    }

    @Override
    public String getAuthority() {
        return name();
    }

    @Override
    public String toString() {
        return "Role{code=" + this.code + "; name=" + this.name() + "}";
    }
}
