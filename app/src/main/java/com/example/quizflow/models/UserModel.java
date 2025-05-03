package com.example.quizflow.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private long id;
    private String username;
    private String pfp;
    private long coins;

    public UserModel() {}

    public UserModel(long id, String username, String pfp, long coins) {
        this.id = id;
        this.username = username;
        this.pfp = pfp;
        this.coins = coins;
    }

    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPfp() {
        return pfp;
    }
    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public long getCoins() {
        return coins;
    }
    public void setCoins(long coins) {
        this.coins = coins;
    }
}
