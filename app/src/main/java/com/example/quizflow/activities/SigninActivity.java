package com.example.quizflow.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.requests.ResendOtpRequest;
import com.example.quizflow.respones.LoginResponse;
import com.example.quizflow.respones.UserResponse;
import com.example.quizflow.utils.Utilities;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.requests.LoginRequest;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {
    private EditText eTxt_email, eTxt_password;
    private ImageView img_eye;
    private TextView txt_btnSignIn, txt_ForgetPassword, txt_signUp;

    private boolean isPasswordVisible = false;
    private Retrofit2Client retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        retrofitClient = new Retrofit2Client();

        initViews();
    }

    private void initViews() {
        eTxt_email = findViewById(R.id.eTxt_email);
        eTxt_email.setText(getIntent().getStringExtra("PREFILL_EMAIL"));
        eTxt_email.setText(getIntent().getStringExtra("SIGNIN_EMAIL"));

        eTxt_password = findViewById(R.id.eTxt_password);

        // password visibility toggle
        img_eye = findViewById(R.id.img_eye);
        img_eye.setOnClickListener(v -> {
            // animate
            img_eye.setAlpha(0.5f);
            new Handler().postDelayed(() -> img_eye.setAlpha(1.0f), 200);

            if (isPasswordVisible) {
                eTxt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img_eye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeoff_white));
            } else {
                eTxt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img_eye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeon_white));
            }
            isPasswordVisible = !isPasswordVisible;
            eTxt_password.setSelection(eTxt_password.length()); // keep cursor at end
        });

        // sign in action
        txt_btnSignIn = findViewById(R.id.txt_btnSignUp);
        txt_btnSignIn.setOnClickListener(this::signIn);

        // forget password redirect
        txt_ForgetPassword = findViewById(R.id.txt_ForgetPassword);
        txt_ForgetPassword.setOnClickListener(v -> {
            // animate
            txt_ForgetPassword.setAlpha(0.5f);
            new Handler().postDelayed(() -> txt_ForgetPassword.setAlpha(1.0f), 200);

            // validate email
            if (validateEmail()) {
                return;
            }

            Retrofit2Client retrofit2Client = new Retrofit2Client();
            ResendOtpRequest resendOtpRequest = new ResendOtpRequest(eTxt_email.getText().toString().trim());
            Call<ResponseBody> call = retrofit2Client.getAPI().forgotPassword(resendOtpRequest);
            call.enqueue(new retrofit2.Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast t = Toast.makeText(SigninActivity.this, "OTP sent", Toast.LENGTH_SHORT);
                        t.show();
                        new android.os.Handler().postDelayed(t::cancel, 1200);

                        Intent intent = new Intent(SigninActivity.this, PasswordActivity.class);
                        intent.putExtra("FORGET_EMAIL", resendOtpRequest.getEmail());
                        intent.putExtra("isForget", true);
                        startActivity(intent);
                        //finish();
                    } else {
                        String msg = "Unknown error";
                        if (response.errorBody() != null) {
                            try {
                                msg = response.errorBody().string();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Utilities.showError(SigninActivity.this, "QF_ERR_SIGNIN", "Error: " + msg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Utilities.showError(SigninActivity.this, "QF_ERR_SIGNIN","Failure: " + t.getMessage());
                }
            });
        });

        // sign up redirect
        txt_signUp = findViewById(R.id.txt_signUp);
        txt_signUp.setText(getSpannableString());
        txt_signUp.setMovementMethod(new android.text.method.LinkMovementMethod()); // enable click event
        txt_signUp.setHighlightColor(Color.TRANSPARENT);
    }

    private void signIn(View view) {
        if (validateEmailOrUsername()) {
            return;
        }
        if (validatePassword()) {
            return;
        }

        // TODO: Transfer to Signup Activity if account doesn't exist
        txt_btnSignIn.setEnabled(false);

        // Create login request
        String email = eTxt_email.getText().toString().trim();
        String password = eTxt_password.getText().toString().trim();
        LoginRequest loginRequest = new LoginRequest(email, password);

        retrofitClient.getAPI().signIn(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                txt_btnSignIn.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Long uid = loginResponse.getUser().getUid();
                    Log.d("SigninActivity", "Storing UID: " + uid);
                    Utilities.setUID(SigninActivity.this, uid);

                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                    intent.putExtra("signedIn", true);
                    startActivity(intent);
                    finish();

                    Toast.makeText(SigninActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Email hoặc mật khẩu không đúng!";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("SIGNIN_ERR", "Error parsing error response", e);
                    }
                    Toast.makeText(SigninActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                txt_btnSignIn.setEnabled(true);
                Log.e("SIGNIN_ERR", "Network error: " + t.getMessage(), t);
                Toast.makeText(SigninActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // for partially clickable, underlined, different color text in a string, in this case: "New to Quizflow? >>Sign up now!<<"
    @NonNull
    private SpannableString getSpannableString() {
        SpannableString spannable = new SpannableString("New to Quizflow? Sign up now!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // animate
                txt_signUp.setAlpha(0.5f);
                new Handler().postDelayed(() -> txt_signUp.setAlpha(1.0f), 100);

                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                intent.putExtra("PREFILL_EMAIL", eTxt_email.getText().toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);      // ensure underline
                ds.setColor(getResources().getColor(R.color.xanh_ngoc));    // to change text color
            }
        };

        spannable.setSpan(clickableSpan, 17, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private boolean validateEmailOrUsername() {
        String email = eTxt_email.getText().toString();
        if (email.isEmpty()) {
            eTxt_email.setError("Please enter your email or username");
            return true;
        } // TODO: email/username existence check

        eTxt_email.setError(null);
        return false;
    }

    private boolean validateEmail() {
        String email = eTxt_email.getText().toString();
        if (email.isEmpty()) {
            eTxt_email.setError("Please enter your email");
            return true;
        } else if (Utilities.isNotValidEmail(email)) {
            eTxt_email.setError("Please enter a valid email");
            return true;
        }

        eTxt_email.setError(null);
        return false;
    }

    private boolean validatePassword() {
        String password = eTxt_password.getText().toString();
        if (password.isEmpty()) {
            eTxt_password.setError("Please enter your password");
            return true;
        } else if (Utilities.isNotValidPassword(password)) {
            eTxt_password.setError("Password must be at least 6 characters and contain at least one digit, one lowercase letter, one uppercase letter, and one special character");
            return true;
        }

        eTxt_password.setError(null);
        return false;
    }
}