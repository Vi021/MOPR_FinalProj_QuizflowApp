package com.example.quizflow;

import com.example.quizflow.models.QuizModel;
import com.example.quizflow.requests.LoginRequest;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.requests.ResendOtpRequest;
import com.example.quizflow.requests.ResetPasswordRequest;
import com.example.quizflow.requests.VerifyOtpRequest;
import com.example.quizflow.respones.APIResponse;
import com.example.quizflow.utils.Refs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    // Register Account
    @POST(Refs.AUTH_URL + "register")
    Call<ResponseBody> register(@Body RegisterRequest request);

    // Sign up
    @POST(Refs.AUTH_URL + "sign-up")
    Call<ResponseBody> signUp(@Body RegisterRequest request);

    // Resend OTP
    @POST(Refs.AUTH_URL + "resend-otp")
    Call<ResponseBody> resendOtp(@Body ResendOtpRequest request);

    // Verify OTP
    @POST(Refs.AUTH_URL + "verify-otp")
    Call<ResponseBody> verifyOtp(@Body VerifyOtpRequest request);

    // Login
    @POST(Refs.AUTH_URL + "login")
    Call<APIResponse> signIn(@Body LoginRequest request);

    // Forgot Password (Send OTP)
    @POST(Refs.AUTH_URL + "forgot-password")
    Call<ResponseBody> forgotPassword(@Body ResendOtpRequest request);

    @POST(Refs.AUTH_URL + "update-password")
    Call<ResponseBody> updatePassword(@Body ResetPasswordRequest request);

    // Reset Password
    @POST(Refs.AUTH_URL + "reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body String username);

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body Long uid);

    @POST(Refs.QUIZ_URL + "{id}")
    Call<APIResponse> getQuiz(@Body Long qid);

    @POST(Refs.QUIZ_URL + "create")
    Call<ResponseBody> saveQuiz(@Body QuizModel quiz);
}
