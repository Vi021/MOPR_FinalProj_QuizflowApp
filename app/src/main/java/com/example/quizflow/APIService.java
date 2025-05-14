package com.example.quizflow;

import com.example.quizflow.requests.ForgotPasswordRequest;
import com.example.quizflow.requests.LoginRequest;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.requests.ResendOtpRequest;
import com.example.quizflow.requests.ResetPasswordRequest;
import com.example.quizflow.requests.VerifyOtpRequest;
import com.example.quizflow.respones.APIResponse;
import com.example.quizflow.respones.LoginResponse;
import com.example.quizflow.respones.UserResponse;
import com.example.quizflow.utils.Refs;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
    Call<LoginResponse> signIn(@Body LoginRequest request);

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

    @GET(Refs.AUTH_URL + "users/{uid}")
    Call<UserResponse> getUserByUid(@Path("uid") String uid);
    @Multipart
    @POST(Refs.AUTH_URL + "update-profile")
    Call<Map<String, Object>> updateProfileWithUid(
            @Part("uid") RequestBody uid,
            @Part("username") RequestBody username,
            @Part("fullname") RequestBody fullname,
            @Part("email") RequestBody email,
            @Part MultipartBody.Part image
    );
}
