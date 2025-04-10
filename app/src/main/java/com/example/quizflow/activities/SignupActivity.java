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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;

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
        // TODO: validate, implement sign-up logic
    }

    // for partially clickable, underlined, different color text in a string, in this case: "Already have an account? >>Sign in instead!<<"
    @NonNull
    private SpannableString getSpannableString() {
        SpannableString spannable = new SpannableString("Already have an account? Sign in instead!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);      // ensure underline
                ds.setColor(getResources().getColor(R.color.xanh_ngoc));    // to change text color
            }
        };

        spannable.setSpan(clickableSpan, 25, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}