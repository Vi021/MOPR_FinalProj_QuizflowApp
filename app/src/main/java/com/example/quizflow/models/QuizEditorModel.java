package com.example.quizflow.models;

import java.io.Serializable;
import java.util.List;

public class QuizEditorModel implements Serializable {
    private QuizModel quiz;
    private List<QuestionModel> questions;

    public QuizEditorModel() {
    }

    public QuizEditorModel(QuizModel quiz, List<QuestionModel> questions) {
        this.quiz = quiz;
        this.questions = questions;
    }

    public QuizModel getQuiz() {
        return quiz;
    }
    public void setQuiz(QuizModel quiz) {
        this.quiz = quiz;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }
    public void setQuestions(List<QuestionModel> questions) {
        this.questions = questions;
    }
}
