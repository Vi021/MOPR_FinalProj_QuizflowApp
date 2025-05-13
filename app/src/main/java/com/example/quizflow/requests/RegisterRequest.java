package com.example.quizflow.requests;

import androidx.annotation.NonNull;

public class RegisterRequest {
    String email;
    String fullname;
    String username;
    String password = "123@Abc";

    public RegisterRequest() {
    }

    public RegisterRequest(@NonNull String email, String fullname, @NonNull String username, String password) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}
