package com.example.quizflow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<List<QuizModel>> quizzes = new MutableLiveData<>();

    public LiveData<List<QuizModel>> getQuizzes() {
        return quizzes;
    }

    public void fetchQuizzes(String query) {
        // TODO: fetch quizzes
    }
}
