package com.example.quizflow.requests;

public class ResetPasswordRequest {
    private Long uid;
    private String oldPassword;
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(Long uid, String oldPassword, String newPassword) {
        this.uid = uid;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public Long getUid() {
        return uid;
    }
    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
