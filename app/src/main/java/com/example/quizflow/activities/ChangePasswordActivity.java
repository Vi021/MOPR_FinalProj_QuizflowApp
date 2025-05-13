package com.example.quizflow.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;
import com.example.quizflow.utils.Utilities;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView img_back, img_odlEye, img_eye, img_retypeEye;
    private EditText eTxt_oldPassword, eTxt_password, eTxt_retypePassword;
    private TextView txt_decorPassword, txt_btnDone;

    private boolean isOldPasswordVisible = false, isPasswordVisible = false, isRetypePasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

    }

    private void initViews() {
        // old password visibility toggle
        img_odlEye = findViewById(R.id.img_odlEye);
        img_odlEye.setOnClickListener(v -> {
            // animate
            img_odlEye.setAlpha(0.5f);
            new Handler().postDelayed(() -> img_odlEye.setAlpha(1.0f), 200);

            if (isPasswordVisible) {
                eTxt_oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img_odlEye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeoff_white));
            } else {
                eTxt_oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img_odlEye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeon_white));
            }
            isOldPasswordVisible = !isOldPasswordVisible;
            eTxt_oldPassword.setSelection(eTxt_oldPassword.length()); // keep cursor at end
        });

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

        // retype password visibility toggle
        img_retypeEye = findViewById(R.id.img_retypeEye);
        img_retypeEye.setOnClickListener(v -> {
            // animate
            img_retypeEye.setAlpha(0.5f);
            new Handler().postDelayed(() -> img_retypeEye.setAlpha(1.0f), 200);

            if (isRetypePasswordVisible) {
                eTxt_retypePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img_retypeEye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeoff_white));
            } else {
                eTxt_retypePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img_retypeEye.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_eyeon_white));
            }
            isRetypePasswordVisible = !isRetypePasswordVisible;
            eTxt_retypePassword.setSelection(eTxt_retypePassword.length()); // keep cursor at end
        });

        // done action
        txt_btnDone = findViewById(R.id.txt_btnDone);
        txt_btnDone.setOnClickListener(v -> {
            if (validatePasswords()) {
                return;
            }

            // TODO: update password

            Toast t = Toast.makeText(this, "You can sign in now", Toast.LENGTH_SHORT);
            t.show();
            finish();
        });
    }

    private boolean validatePasswords() {
        if (eTxt_oldPassword.getText().toString().isEmpty()) {
            eTxt_oldPassword.setError("Old password is required");
            return true;
        } // TODO: old password check
        eTxt_oldPassword.setError(null);

        if (eTxt_password.getText().toString().isEmpty()) {
            eTxt_password.setError("Password is required");
            return true;
        } else if (Utilities.isNotValidPassword(eTxt_password.getText().toString())) {
            eTxt_password.setError("Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and length of 6+");
            return true;
        } else if (eTxt_password.getText().toString().equals(eTxt_oldPassword.getText().toString())) {
            eTxt_password.setError("Please enter a new password");
            return true;
        } else if (!eTxt_retypePassword.getText().toString().equals(eTxt_password.getText().toString())) {
            eTxt_password.setError(null);
            eTxt_retypePassword.setError("Passwords do not match");
            return true;
        } // TODO: password update

        eTxt_retypePassword.setError(null);
        return false;
    }
}