package com.example.quizflow.requests;

public class QuizAnswerRequest {
    private Long lid;
    private Long uid;
    private Long qtid;
    private Long aid;

    public Long getLid() { return lid; }
    public void setLid(Long lid) { this.lid = lid; }
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }
    public Long getQtid() { return qtid; }
    public void setQtid(Long qtid) { this.qtid = qtid; }
    public Long getAid() { return aid; }
    public void setAid(Long aid) { this.aid = aid; }
}
