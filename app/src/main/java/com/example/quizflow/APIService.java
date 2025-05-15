package com.example.quizflow;

import com.example.quizflow.models.QuizEditorModel;
import com.example.quizflow.requests.CoinHistoryRequest;
import com.example.quizflow.requests.CoinUpdateRequest;
import com.example.quizflow.requests.JoinLobbyRequest;
import com.example.quizflow.requests.LobbyRequest;
import com.example.quizflow.requests.LoginRequest;
import com.example.quizflow.requests.QuizResponseRequest;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.requests.ResendOtpRequest;
import com.example.quizflow.requests.ForgetPasswordRequest;
import com.example.quizflow.requests.ResetPasswordRequest;
import com.example.quizflow.requests.StartLobbyRequest;
import com.example.quizflow.requests.VerifyOtpRequest;
import com.example.quizflow.respones.APIResponse;
import com.example.quizflow.respones.AttemptRequest;
import com.example.quizflow.respones.AttemptResponse;
import com.example.quizflow.respones.LobbyResponse;
import com.example.quizflow.respones.LoginResponse;
import com.example.quizflow.respones.QuizResponse;
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
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Call<ResponseBody> forgotPassword(@Body ResendOtpRequest request);

    @POST(Refs.AUTH_URL + "update-password")
    Call<ResponseBody> updatePassword(@Body ForgetPasswordRequest request);

    // Reset Password
    @POST(Refs.AUTH_URL + "reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);

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

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body String username);

    @POST(Refs.USER_URL)
    Call<APIResponse> getUser(@Body Long uid);

    @POST(Refs.QUIZ_URL + "create")
    Call<ResponseBody> saveQuiz(@Body QuizModel quiz);

    @GET(Refs.QUIZ_URL + "{qid}")
    Call<QuizResponse> getQuizById(@Path("qid") long qid);

    @POST(Refs.QUIZ_URL + "attempts")
    Call<AttemptResponse> createAttempt(@Body AttemptRequest attempt);

    @POST(Refs.QUIZ_URL + "quizResponses")
    Call<Void> createQuizResponse(@Body QuizResponseRequest response);

    @PUT(Refs.AUTH_URL + "{uid}/coins")
    Call<Void> updateUserCoins(@Path("uid") long uid, @Body CoinUpdateRequest coinUpdate);

    @POST(Refs.QUIZ_URL + "coinHistory")
    Call<Void> createCoinHistory(@Body CoinHistoryRequest coinHistory);

    @POST("api/lobby/create")
    Call<LobbyResponse> createLobby(@Body LobbyRequest request);

    @POST("api/lobby/join")
    Call<LobbyResponse> joinLobby(@Body JoinLobbyRequest request);

    @GET("api/lobby/{lid}")
    Call<LobbyResponse> getLobbyInfo(@Path("lid") Long lid);

    @POST("api/lobby/{lid}/start")
    Call<Void> startLobby(@Path("lid") Long lid, @Body StartLobbyRequest request);
    Call<Map<String, Long>> createQuiz(@Body QuizEditorModel quiz);

    @POST(Refs.QUIZ_URL + "update")
    Call<ResponseBody> updateQuiz(@Body QuizEditorModel quiz);

    @GET(Refs.QUIZ_URL + "{qid}/edit") // Use @GET not @POST
    Call<QuizEditorModel> getQuizEditor(@Path("qid") Long qid, @Query("uid") Long uid);

    @GET("api/quiz/{qid}")
    Call<QuizResponse> getQuizById(@Path("qid") Long qid);
}