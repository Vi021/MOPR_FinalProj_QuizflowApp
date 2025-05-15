package com.example.quizflow.requests;

public class QuizResponseRequest {
    private String answer;
    private long qtid;
    private long atid;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getQtid() {
        return qtid;
    }

    public void setQtid(long qtid) {
        this.qtid = qtid;
    }

    public long getAtid() {
        return atid;
    }

    public void setAtid(long atid) {
        this.atid = atid;
    }
}
