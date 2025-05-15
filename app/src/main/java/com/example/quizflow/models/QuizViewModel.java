package com.example.quizflow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.utils.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<List<QuizModel>> quizzes = new MutableLiveData<>();

    public LiveData<List<QuizModel>> getQuizzes() {
        return quizzes;
    }

    public void fetchQuizzes(String query) {
        Retrofit2Client.getAPI().getPublicQuizzesByKeyword(query).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizzes.setValue(response.body());
                } else {
                    quizzes.setValue(null);
                    Utilities.showError(null, "QF_SRCH_QUIZ", "Failed to fetch quizzes");
                }
            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                quizzes.setValue(null);
                Utilities.showError(null, "QF_SRCH_QUIZ", t.getMessage());
            }
        });
    }
}
