package com.example.quizflow;

import com.example.quizflow.requests.ForgotPasswordRequest;
import com.example.quizflow.requests.LoginRequest;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.requests.ResetPasswordRequest;
import com.example.quizflow.requests.VerifyOtpRequest;
import com.example.quizflow.respones.APIResponse;
import com.example.quizflow.utils.Refs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    // Register Account
    @POST(Refs.AUTH_URL + "register")
    Call<APIResponse> signUp(@Body RegisterRequest request);

    // Verify OTP
    @POST(Refs.AUTH_URL + "verify-otp")
    Call<APIResponse> verifyOtp(@Body VerifyOtpRequest request);

    // Login
    @POST(Refs.AUTH_URL + "login")
    Call<APIResponse> signIn(@Body LoginRequest request);

    // Forgot Password (Send OTP)
    @POST(Refs.AUTH_URL + "forgot-password")
    Call<APIResponse> forgotPassword(@Body ForgotPasswordRequest request);

    // Reset Password
    @POST(Refs.AUTH_URL + "reset-password")
    Call<APIResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body String username);

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body Long uid);
}
