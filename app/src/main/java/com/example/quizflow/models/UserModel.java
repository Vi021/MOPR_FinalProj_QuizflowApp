package com.example.quizflow.models;

public class UserModel {
    private int id;
    private String username;
    private String pfp;
    private long score;

    public UserModel() {}

    public UserModel(int id, String username, String pfp, long score) {
        this.id = id;
        this.username = username;
        this.pfp = pfp;
        this.score = score;
    }

    public int getId() {
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

    public long getScore() {
        return score;
    }
    public void setScore(long score) {
        this.score = score;
    }
}
