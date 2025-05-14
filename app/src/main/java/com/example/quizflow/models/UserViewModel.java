package com.example.quizflow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<List<UserModel>> users = new MutableLiveData<>();

    public LiveData<List<UserModel>> getUsers() {
        return users;
    }

    public void fetchUsers(String query) {
        // TODO: fetch users
    }
}
