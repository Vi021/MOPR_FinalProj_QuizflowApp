package com.example.quizflow.activities;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;

public class PasswordActivity extends AppCompatActivity {
    private ImageView img_back, img_eye, img_retypeEye;
    private EditText eTxt_password, eTxt_retypePassword;
    private TextView txt_btnDone;

    private boolean isPasswordVisible = false;
    private boolean isRetypePasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);
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
        // return action
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        // password input
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

        // retype password input
        eTxt_retypePassword = findViewById(R.id.eTxt_retypePassword);

        // retype password visibility toggle
        img_retypeEye = findViewById(R.id.img_retypeEye);
        img_retypeEye.setOnClickListener(v -> {
            if (isRetypePasswordVisible) {
                eTxt_retypePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img_retypeEye.setImageResource(R.drawable.ic_eyeoff_white);
            } else {
                eTxt_retypePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img_retypeEye.setImageResource(R.drawable.ic_eyeon_white);
            }
            isRetypePasswordVisible = !isRetypePasswordVisible;
            eTxt_retypePassword.setSelection(eTxt_retypePassword.length()); // keep cursor at end
        });

        // done action
        txt_btnDone = findViewById(R.id.txt_btnDone);
    }
}