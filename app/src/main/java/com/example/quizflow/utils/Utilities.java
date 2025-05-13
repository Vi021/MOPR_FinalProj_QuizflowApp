package com.example.quizflow.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.quizflow.models.UserModel;

import java.util.Locale;

public class Utilities {
    public static void showError(Context context, String tag, String message) {
        Log.e(tag, message);
        Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        t.show();
        new android.os.Handler().postDelayed(t::cancel, 1200);
    }

    public static UserModel getUser() {
        // TODO: get current user
        return null;
    }
    public static UserModel getUser(long uid) {
        // TODO: get user via uid
        return null;
    }
    public static UserModel getUser(String emailOrUsername) {
        // TODO: get user via email/username
        return null;
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
