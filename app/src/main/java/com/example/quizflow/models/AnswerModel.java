package com.example.quizflow.models;

public class AnswerModel {
    private long aid;
    private String text = "";
    private boolean isCorrect = false;
    private long qtid;

    public AnswerModel() { }

    public AnswerModel(long aid, String text, boolean isCorrect, long qid) {
        this.aid = aid;
        this.text = text;
        this.isCorrect = isCorrect;
        this.qtid = qid;
    }

    public long getAid() {
        return aid;
    }
    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public long getQtid() {
        return qtid;
    }
    public void setQtid(long qtid) {
        this.qtid = qtid;
    }
}
