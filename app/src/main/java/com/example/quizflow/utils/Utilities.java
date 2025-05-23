package com.example.quizflow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.quizflow.APIService;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.requests.CoinHistoryRequest;
import com.example.quizflow.requests.CoinUpdateRequest;
import com.example.quizflow.requests.JoinLobbyRequest;
import com.example.quizflow.requests.LobbyRequest;
import com.example.quizflow.requests.QuizResponseRequest;
import com.example.quizflow.requests.StartLobbyRequest;
import com.example.quizflow.respones.AttemptRequest;
import com.example.quizflow.respones.AttemptResponse;
import com.example.quizflow.respones.LobbyResponse;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.respones.UserResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utilities {

    private static final String PREFS_NAME = "QuizFlowPrefs";
    private static final String KEY_UID = "uid";
    private static Retrofit2Client retrofitClient;

    // Initialize Retrofit client
    private static void initRetrofit() {
        if (retrofitClient == null) {
            retrofitClient = new Retrofit2Client();
        }
    }


    // Store UID in SharedPreferences
    public static void setUID(Context context, Long uid) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_UID, String.valueOf(uid)).apply();
        Log.d("Utilities", "Stored UID: " + uid);
    }

    // Clear UID from SharedPreferences
    public static void clearUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_UID).apply();
        Log.d("Utilities", "Cleared UID");
    }

    // Get UID from SharedPreferences
    public static Long getUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uidStr = prefs.getString(KEY_UID, null);
        try {
            Long uid = uidStr != null ? Long.parseLong(uidStr) : null;
            Log.d("Utilities", "Retrieved UID: " + uid);
            return uid;
        } catch (NumberFormatException e) {
            Log.e("Utilities", "Invalid UID format: " + uidStr);
            return null;
        }
    }

    // Asynchronous callback interface
    public interface AccountCallback {
        void onSuccess(AccountModel account);
        void onFailure(String error);
    }

    // Fetch user by UID asynchronously from API
    public static void getUserByUidAsync(Context context, long uid, AccountCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().getUserByUid(String.valueOf(uid)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    AccountModel account = new AccountModel();
                    account.setUid(userResponse.getUid());
                    account.setUsername(userResponse.getUsername());
                    account.setFullname(userResponse.getFullname());
                    account.setEmail(userResponse.getEmail());
                    account.setImage(userResponse.getImage());
                    account.setCoins(userResponse.getCoins());
                    Log.d("Utilities", "Fetched user from API: UID=" + uid);
                    callback.onSuccess(account);
                } else {
                    String error = "Failed to fetch user, HTTP " + response.code();
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", error);
                callback.onFailure(error);
            }
        });
    }

    public static void showError(Context context, String tag, String message) {
        Log.e(tag, message);
//        if (context != null) {
//            Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//            t.show();
//            new android.os.Handler().postDelayed(t::cancel, 1200);
//        }
    }

    public static String toUpperUnderscore(String input) {
        return input.trim()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "_");
    }

    public static String toTitleCase(String input) {
        String lower = input.trim().toLowerCase(Locale.ROOT).replace("_", " ");
        String[] words = lower.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    public static String toLowerCase(String input) {
        return input.trim().toLowerCase(Locale.ROOT).replace("_", " ").trim();
    }

    public static boolean isNotValidEmail(String email) {
        // check if the email is valid
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isNotValidPassword(String password) {
        // check if the password contains at least one digit, one lowercase letter, one uppercase letter, and one special character, length of 6+
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$";
        return !(password != null && password.matches(passwordRegex));
    }

    public static boolean isNotValidUsername(String username) {
        // check if the username contains only letters, digits, underscores, ond 3-20 in length
        return !(username != null && username.trim().matches("^[a-zA-Z0-9_]{3,20}$"));
    }

    public static boolean isNotValidFullname(String name) {
        // check if the fullname(UniKey) contains only letters, spaces, apostrophes('), and 2-40 in length
        return name == null || !name.trim().matches("^[\\p{L}' ]{2,40}$");
    }

    public static boolean isNotValidPhoneNumber(String phoneNumber) {
        // check if the phone number is valid
        return !android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static boolean isNotValidQuizId(String quizId) {
        try {
            long id = Long.parseLong(quizId.trim());
            // Quiz IDs should be 6 digits or longer (100000+)
            return id < 100000;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    // Fetch quiz by qid asynchronously from API
    public static void getQuizByIdAsync(Context context, long qid, QuizCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().getQuizById(qid).enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to fetch quiz, HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create attempt asynchronously
    public static void createAttemptAsync(Context context, AttemptRequest attempt, AttemptCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().createAttempt(attempt).enqueue(new Callback<AttemptResponse>() {
            @Override
            public void onResponse(Call<AttemptResponse> call, Response<AttemptResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to create attempt, HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AttemptResponse> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create quiz response asynchronously
    public static void createQuizResponseAsync(Context context, QuizResponseRequest response, GenericCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().createQuizResponse(response).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Failed to save response, HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Update user coins asynchronously
    public static void updateUserCoinsAsync(Context context, long uid, int coins, GenericCallback callback) {
        initRetrofit();
        CoinUpdateRequest request = new CoinUpdateRequest();
        request.setCoins(coins);
        Retrofit2Client.getAPI().updateUserCoins(uid, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Failed to update coins, HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create coin history asynchronously
    public static void createCoinHistoryAsync(Context context, CoinHistoryRequest coinHistory, GenericCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().createCoinHistory(coinHistory).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Failed to save coin history, HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create lobby asynchronously
    public static void createLobbyAsync(Context context, LobbyRequest request, LobbyCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().createLobby(request).enqueue(new Callback<LobbyResponse>() {
            @Override
            public void onResponse(Call<LobbyResponse> call, Response<LobbyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to create lobby, HTTP " + response.code();
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<LobbyResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", error);
                callback.onFailure(error);
            }
        });
    }

    // Join lobby asynchronously
    public static void joinLobbyAsync(Context context, JoinLobbyRequest request, LobbyCallback callback) {
        initRetrofit();
        Log.d("Utilities", "Sending join lobby request with code: " + request.getCode() + ", uid: " + request.getUid());
        Retrofit2Client.getAPI().joinLobby(request).enqueue(new Callback<LobbyResponse>() {
            @Override
            public void onResponse(Call<LobbyResponse> call, Response<LobbyResponse> response) {
                Log.d("Utilities", "Join lobby response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    LobbyResponse lobby = response.body();
                    Log.d("Utilities", "Join lobby successful: lid=" + lobby.getLid() + ", code=" + lobby.getCode() + ", qid=" + lobby.getQid());
                    callback.onSuccess(lobby);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("Utilities", "Error reading error body: " + e.getMessage());
                    }
                    String error = "Failed to join lobby, HTTP " + response.code() + ": " + errorBody;
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<LobbyResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", "Join lobby network error: " + t.getMessage());
                t.printStackTrace();
                callback.onFailure(error);
            }
        });
    }

    // Start lobby asynchronously
    public static void startLobbyAsync(Context context, Long lid, Long uid, GenericCallback callback) {
        initRetrofit();
        StartLobbyRequest request = new StartLobbyRequest();
        request.setUid(uid);
        Log.d("Utilities", "Starting lobby with lid=" + lid + ", uid=" + uid);
        
        Retrofit2Client.getAPI().startLobby(lid, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Utilities", "Start lobby response: " + response.code() + " " + response.message());
                if (response.isSuccessful()) {
                    Log.d("Utilities", "Successfully started lobby");
                    callback.onSuccess();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("Utilities", "Error reading error body: " + e.getMessage());
                    }
                    String error = "Failed to start lobby, HTTP " + response.code() + ": " + errorBody;
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", "Start lobby network error: " + t.getMessage());
                t.printStackTrace();
                callback.onFailure(error);
            }
        });
    }

    // Get lobby info asynchronously
    public static void getLobbyInfoAsync(Context context, Long lid, LobbyInfoCallback callback) {
        initRetrofit();
        Retrofit2Client.getAPI().getLobbyInfo(lid).enqueue(new Callback<LobbyResponse>() {
            @Override
            public void onResponse(Call<LobbyResponse> call, Response<LobbyResponse> response) {
                Log.d("Utilities", "Get lobby info response: " + response.code() + " " + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Utilities", "Successfully got lobby info with qid: " + response.body().getQid());
                    callback.onSuccess(response.body().getQid());
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("Utilities", "Error reading error body: " + e.getMessage());
                    }
                    String error = "Failed to get lobby info, HTTP " + response.code() + ": " + errorBody;
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<LobbyResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", "Get lobby info network error: " + t.getMessage());
                t.printStackTrace();
                callback.onFailure(error);
            }
        });
    }

    public interface QuizCallback {
        void onSuccess(QuizResponse quiz);
        void onFailure(String error);
    }

    public interface AttemptCallback {
        void onSuccess(AttemptResponse attempt);
        void onFailure(String error);
    }

    public interface GenericCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface LobbyCallback {
        void onSuccess(LobbyResponse lobby);

        void onFailure(String error);
    }

    public interface LobbyInfoCallback {
        void onSuccess(Long qid);
        void onFailure(String error);
    }

    // Check if lobby code exists asynchronously
    public interface LobbyCodeCallback {
        void onSuccess(boolean exists);
        void onFailure(String error);
    }
    
    public static void checkLobbyCodeAsync(Context context, String code, LobbyCodeCallback callback) {
        initRetrofit();
        Log.d("Utilities", "Checking if lobby code exists: " + code);
        Retrofit2Client.getAPI().checkLobbyCode(code).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("Utilities", "Check lobby code response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = response.body();
                    Log.d("Utilities", "Lobby code " + code + " exists: " + exists);
                    callback.onSuccess(exists);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("Utilities", "Error reading error body: " + e.getMessage());
                    }
                    String error = "Failed to check lobby code, HTTP " + response.code() + ": " + errorBody;
                    Log.e("Utilities", error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("Utilities", "Check lobby code network error: " + t.getMessage());
                t.printStackTrace();
                callback.onFailure(error);
            }
        });
    }
}
