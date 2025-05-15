package com.example.quizflow.models;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    private long aid;
    private String text = "";
    private boolean correct = false;
    private long qtid;

    public AnswerModel() { }

    public AnswerModel(long aid, String text, boolean correct, long qid) {
        this.aid = aid;
        this.text = text;
        this.correct = correct;
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
        return correct;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public long getQtid() {
        return qtid;
    }
    public void setQtid(long qtid) {
        this.qtid = qtid;
    }
}
