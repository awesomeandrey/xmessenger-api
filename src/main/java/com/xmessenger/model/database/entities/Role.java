package com.xmessenger.model.database.entities;

public enum Role {

    USER(1), ADMIN(2);

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
                return Role.USER;
            case 2:
                return Role.ADMIN;
            default:
                return Role.USER;
        }
    }
}
