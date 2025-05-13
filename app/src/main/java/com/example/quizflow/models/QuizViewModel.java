package com.example.quizflow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<List<QuizModel>> quizzes = new MutableLiveData<>();
    //private final QuizRepository quizRepository = new QuizRepository();

    public LiveData<List<QuizModel>> getQuizzes() {
        return quizzes;
    }

    public void fetchQuizzes(String query) {
//        quizRepository.searchQuizzes(query, new QuizRepository.Callback() {
//            @Override
//            public void onSuccess(List<QuizModel> result) {
//                quizzes.postValue(result);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                // handle error
//            }
//        });
    }
}
