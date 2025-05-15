package com.example.quizflow.requests;

public class ForgetPasswordRequest {
    private String email;
    private String password;

    public ForgetPasswordRequest() {
    }

    public ForgetPasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
