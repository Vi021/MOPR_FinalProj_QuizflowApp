package com.example.quizflow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.models.AccountModel;
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
        retrofitClient.getAPI().getUserByUid(String.valueOf(uid)).enqueue(new Callback<UserResponse>() {
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
        Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        t.show();
        new android.os.Handler().postDelayed(t::cancel, 1200);
    }

    public static String toUpperUnderscore(String input) {
        return input.trim()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "_");
    }

    public static String toTitleCase(String input) {
        String lower = input.toLowerCase(Locale.ROOT).replace("_", " ");
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
        return input.toLowerCase(Locale.ROOT).replace("_", " ").trim();
    }

    public static boolean isNotValidEmail(String email) {
        // check if the email is valid
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isEmailAvailable(String email) {
        // TODO: email availability check
        return false;
    }

    public static boolean isNotValidPassword(String password) {
        // check if the password contains at least one digit, one lowercase letter, one uppercase letter, and one special character, length of 6+
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$";
        return !(password != null && password.matches(passwordRegex));
    }

    public static boolean isNotValidUsername(String username) {
        // check if the username contains only letters, digits, underscores, ond 3-20 in length
        return !(username != null && username.matches("^[a-zA-Z0-9_]{3,20}$"));
    }

    public static boolean isUsernameAvailable(String fullname) {
        // TODO: username availability check
        return false;
    }

    public static boolean isNotValidFullname(String name) {
        // check if the fullname(UniKey) contains only letters, spaces, apostrophes('), and 2-40 in length
        return name == null || !name.trim().matches("^[\\p{L}' ]{2,40}$");
    }

    public static boolean isNotValidPhoneNumber(String phoneNumber) {
        // check if the phone number is valid
        return !android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }
}
