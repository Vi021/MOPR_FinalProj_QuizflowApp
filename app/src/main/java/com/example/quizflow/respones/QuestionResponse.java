package com.example.quizflow.respones;

import java.util.List;

public class QuestionResponse {
    private long qtid;
    private String question;
    private String type;
    private List<AnswerResponse> answers;

    public long getQtid() {
        return qtid;
    }

    public void setQtid(long qtid) {
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

    public List<AnswerResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerResponse> answers) {
        this.answers = answers;
    }
}
