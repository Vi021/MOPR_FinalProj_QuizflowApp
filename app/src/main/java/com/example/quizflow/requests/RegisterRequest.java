package com.example.quizflow.requests;

import androidx.annotation.NonNull;

public class RegisterRequest {
    String email;
    String fullname;
    String username;

    public RegisterRequest() {
    }

    public RegisterRequest(@NonNull String email, String fullname, @NonNull String username) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
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
        // This method is not used in this class, but it's here to match the original code structure.
    }
    public String getPassword() {
        return "123@Abc"; // This method is not used in this class, but it's here to match the original code structure.
    }
}
