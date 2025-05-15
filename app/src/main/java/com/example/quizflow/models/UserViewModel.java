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

public class UserViewModel extends ViewModel {
    private final MutableLiveData<List<UserModel>> users = new MutableLiveData<>();

    public LiveData<List<UserModel>> getUsers() {
        return users;
    }

    public void fetchUsers(String query) {
        Retrofit2Client.getAPI().getUsersByUsername(query).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.setValue(response.body());
                } else {
                    users.setValue(null);
                    Utilities.showError(null, "QF_SRCH_USER", "Failed to fetch users");
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                users.setValue(null);
                Utilities.showError(null, "QF_SRCH_USER", t.getMessage());
            }
        });
    }
}
