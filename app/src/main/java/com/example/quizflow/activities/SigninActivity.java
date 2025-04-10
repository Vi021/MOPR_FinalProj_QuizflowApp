package com.example.quizflow.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SigninActivity extends AppCompatActivity {
    private EditText eTxt_email, eTxt_password;
    private ImageView img_eye;
    private TextView txt_btnSignIn, txt_ForgetPassword, txt_signUp;

    private boolean isPasswordVisible = false;

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

        // for UI
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        initViews();
    }

    private void initViews() {
        eTxt_email = findViewById(R.id.eTxt_email);
        eTxt_password = findViewById(R.id.eTxt_password);

        // password visibility toggle
        img_eye = findViewById(R.id.img_eye);
        img_eye.setOnClickListener(v -> {
            if (isPasswordVisible) {
                eTxt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img_eye.setImageResource(R.drawable.ic_eyeoff_white);
            } else {
                eTxt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img_eye.setImageResource(R.drawable.ic_eyeon_white);
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
            Intent intent = new Intent(SigninActivity.this, PasswordActivity.class);
            startActivity(intent);
            finish();
        });

        // sign up redirect
        txt_signUp = findViewById(R.id.txt_signUp);
        txt_signUp.setText(getSpannableString());
        txt_signUp.setMovementMethod(new android.text.method.LinkMovementMethod()); // enable click event
        txt_signUp.setHighlightColor(Color.TRANSPARENT);
    }

    private void signIn(View view) {
        // TODO: validate email and handle password
        //  implement sign-in logic
        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // for partially clickable, underlined, different color text in a string, in this case: "New to Quizflow? >>Sign up now!<<"
    @NonNull
    private SpannableString getSpannableString() {
        SpannableString spannable = new SpannableString("New to Quizflow? Sign up now!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
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

        spannable.setSpan(clickableSpan, 17, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}