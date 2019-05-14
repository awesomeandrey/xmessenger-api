package com.xmessenger.model.services.core.user.dao;

public class QueryParams {
    private String nameOrLogin;
    private boolean searchByLogin;

    public String getNameOrLogin() {
        return nameOrLogin;
    }

    public void setNameOrLogin(String nameOrLogin) {
        this.nameOrLogin = nameOrLogin;
    }

    public boolean isSearchByLogin() {
        return searchByLogin;
    }

    public void setSearchByLogin(boolean searchByLogin) {
        this.searchByLogin = searchByLogin;
    }

    public QueryParams() {
        this.searchByLogin = true;
    }

    public QueryParams(String nameOrLogin, boolean searchByLogin) {
        this();
        this.nameOrLogin = nameOrLogin;
        this.searchByLogin = searchByLogin;
    }
}
