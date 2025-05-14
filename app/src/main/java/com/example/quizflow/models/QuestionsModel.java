package com.example.quizflow.models;

import java.io.Serializable;
import java.util.List;

public class QuestionsModel implements Serializable {
    private long qid;
    private List<QuestionModel> questions;

    public QuestionsModel() {
    }

    public QuestionsModel(long qid, List<QuestionModel> questions) {
        this.qid = qid;
        this.questions = questions;
    }

    public long getQid() {
        return qid;
    }
    public void setQid(long qid) {
        this.qid = qid;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }
    public void setQuestions(List<QuestionModel> questions) {
        this.questions = questions;
    }
}
