package com.example.quizflow.requests;

public class JoinLobbyRequest {
    private String code;
    private Long uid;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
}
