package com.example.quizflow.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

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
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.requests.ResetPasswordRequest;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView img_back, img_odlEye, img_eye, img_retypeEye;
    private EditText eTxt_oldPassword, eTxt_password, eTxt_retypePassword;
    private TextView txt_decorPassword, txt_btnDone;

    private boolean isOldPasswordVisible = false, isPasswordVisible = false, isRetypePasswordVisible = false;

    private boolean signedIn = false;

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

        initViews();
    }

    private void initViews() {
        Long uid = Utilities.getUID(this);
        signedIn = (uid != null);

        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        // password inputs
        eTxt_oldPassword = findViewById(R.id.eTxt_oldPassword);
        eTxt_password = findViewById(R.id.eTxt_password);
        eTxt_retypePassword = findViewById(R.id.eTxt_retypePassword);

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

            new Retrofit2Client().getAPI().resetPassword(new ResetPasswordRequest(uid, eTxt_oldPassword.getText().toString(), eTxt_password.getText().toString()))
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast t = Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT);
                                t.show();
                                new Handler().postDelayed(t::cancel, 1200);
                            } else {
                                String error = null;
                                try {
                                    assert response.errorBody() != null;
                                    error = "Error: " + response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Utilities.showError(ChangePasswordActivity.this, "Change Password", error);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            String error = "Network error: " + t.getMessage();
                            Utilities.showError(ChangePasswordActivity.this, "Change Password", error);
                        }
                    });
        });
    }

    private boolean validatePasswords() {
        if (eTxt_oldPassword.getText().toString().isEmpty()) {
            eTxt_oldPassword.setError("Old password is required");
            return true;
        }
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
        }

        eTxt_retypePassword.setError(null);
        return false;
    }
}