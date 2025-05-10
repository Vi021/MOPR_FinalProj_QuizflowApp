package com.example.quizflow.models;

import java.util.List;

public class QuestionModel {
    private Long qtid;
    private String question = "";
    private String type = "MCQ";
    private List<AnswerModel> answers;

    public QuestionModel() { }

    public Long getQtid() {
        return qtid;
    }
    public void setQtid(Long qtid) {
        this.qtid = qtid;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }
    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }
}
