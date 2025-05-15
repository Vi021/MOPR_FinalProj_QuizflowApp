package com.example.quizflow.models;

public class ParticipantStatus {
    private Long uid;
    private String username;
    private Integer score;

    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
