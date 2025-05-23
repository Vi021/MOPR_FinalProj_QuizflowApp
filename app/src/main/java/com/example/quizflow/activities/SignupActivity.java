package com.example.quizflow.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SignupActivity extends AppCompatActivity {
    private EditText eTxt_email, eTxt_fullname, eTxt_username;
    private TextView txt_btnSignUp, txt_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        initViews();
    }

    private void initViews() {
        eTxt_email = findViewById(R.id.eTxt_email);
        eTxt_email.setText(getIntent().getStringExtra("PREFILL_EMAIL"));
        validateEmail();

        eTxt_fullname = findViewById(R.id.eTxt_fullname);
        eTxt_username = findViewById(R.id.eTxt_username);

        // sign up action
        txt_btnSignUp = findViewById(R.id.txt_btnSignUp);
        txt_btnSignUp.setOnClickListener(this::signUp);

        // sign in redirect
        txt_signIn = findViewById(R.id.txt_signIn);
        txt_signIn.setText(getSpannableString());
        txt_signIn.setMovementMethod(new android.text.method.LinkMovementMethod()); // enable click event
        txt_signIn.setHighlightColor(Color.TRANSPARENT);
    }

    private void signUp(View view) {
        if (validateEmail()) {
            return;
        }
        String fullname = eTxt_fullname.getText().toString().trim();
        if (!fullname.isEmpty()) {
            if (Utilities.isNotValidFullname(fullname)) {
                eTxt_fullname.setError("Fullname is optional, but if included, must be 2-40 characters long and contain only letters, spaces");
                return;
            }
        }
        if (validateUsername()) {
            return;
        }

        Retrofit2Client retrofit2Client = new Retrofit2Client();
        RegisterRequest registerRequest = new RegisterRequest(eTxt_email.getText().toString().trim(), fullname, eTxt_username.getText().toString().trim());
        Call<ResponseBody> call = retrofit2Client.getAPI().register(registerRequest);
        call.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast t = Toast.makeText(SignupActivity.this, "OTP sent", Toast.LENGTH_SHORT);
                    t.show();
                    new android.os.Handler().postDelayed(t::cancel, 1200);

                    Intent intent = new Intent(SignupActivity.this, PasswordActivity.class);
                    intent.putExtra("SIGNUP_USER", registerRequest);
                    intent.putExtra("isNew", true);
                    startActivity(intent);
                } else {
                    String msg = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            msg = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Utilities.showError(SignupActivity.this, "QF_ERR_SIGNUP", "Error: " + msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Utilities.showError(SignupActivity.this, "QF_ERR_SIGNUP","Failure: " + t.getMessage());
            }
        });
    }

    // for partially clickable, underlined, different color text in a string, in this case: "Already have an account? >>Sign in instead!<<"
    @NonNull
    private SpannableString getSpannableString() {
        SpannableString spannable = new SpannableString("Already have an account? Sign in instead!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // animate
                txt_signIn.setAlpha(0.5f);
                new android.os.Handler().postDelayed(() -> txt_signIn.setAlpha(1.0f), 100);

                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
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

        spannable.setSpan(clickableSpan, 25, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
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

    private boolean validateUsername() {
        String username = eTxt_username.getText().toString();
        if (username.isEmpty()) {
            eTxt_username.setError("Please enter your username");
            return true;
        } else if (Utilities.isNotValidUsername(username)) {
            eTxt_username.setError("Username must be 3-20 characters long and contain only letters, digits, and underscores");
            return true;
        }

        eTxt_username.setError(null);
        return false;
    }
}