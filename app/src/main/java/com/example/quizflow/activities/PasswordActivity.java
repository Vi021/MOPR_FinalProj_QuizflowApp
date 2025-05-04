package com.example.quizflow.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.quizflow.R;
import com.example.quizflow.utils.Validators;

public class PasswordActivity extends AppCompatActivity {
    private ImageView img_back, img_odlEye, img_eye, img_retypeEye;
    private EditText eTxt_oldPassword, eTxt_password, eTxt_retypePassword;
    private TextView txt_decorPassword, txt_btnDone;

    private boolean isChange = false, isForget = false;
    private boolean isOldPasswordVisible = false, isPasswordVisible = false, isRetypePasswordVisible = false;
    private String email;

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

        //
        isChange = getIntent().getBooleanExtra("isChange", false);
        isForget = getIntent().getBooleanExtra("isForget", false);

        initViews();
    }

    private void initViews() {
        // return action
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        // password inputs
        eTxt_oldPassword = findViewById(R.id.eTxt_oldPassword);
        eTxt_password = findViewById(R.id.eTxt_password);
        eTxt_retypePassword = findViewById(R.id.eTxt_retypePassword);

        // get email
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty() || Validators.isNotValidEmail(email)) {
            Toast t = Toast.makeText(this, "Unable to obtain your email!", Toast.LENGTH_SHORT);
            t.show();
            new Handler().postDelayed(t::cancel, 2000);
            finish();
        } else {
            // show OTP dialog
            new Handler().postDelayed(() -> showOtpDialog(this), 1000);
        }

        // ui
        txt_decorPassword = findViewById(R.id.txt_decorPassword);
        if (isForget) {    // forget pwd
            txt_decorPassword.setText("Reset Your Password");
            eTxt_oldPassword.setVisibility(View.GONE);
            // to change eTxt_oldPassword ToptoBot constraint
            ConstraintLayout parentContainer = findViewById(R.id.consL);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(parentContainer);
            constraintSet.connect(
                    R.id.lineL_passwordField,
                    ConstraintSet.TOP,
                    R.id.txt_decorPassword,
                    ConstraintSet.BOTTOM
            );
            constraintSet.applyTo(parentContainer);
        } else if (isChange) {    // change pwd
            txt_decorPassword.setText("Change Your Password");
        }

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

    public void showOtpDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.diaglog_otp, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false);   // no exit on back press or outside touch
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView img_close = dialogView.findViewById(R.id.img_close);
        TextView txt_otpDesc = dialogView.findViewById(R.id.txt_otpDesc);
        EditText eTxt_otp = dialogView.findViewById(R.id.eTxt_otp);
        Button btn_verifyOTP = dialogView.findViewById(R.id.btn_verifyOTP);
        TextView txt_resendOTP = dialogView.findViewById(R.id.txt_resendOTP);
        LinearLayout lineL_dialogOTP = dialogView.findViewById(R.id.lineL_otpDialog);

        // close action
        img_close.setOnClickListener(v -> {
            // animate
            img_close.setAlpha(0.5f);
            new Handler().postDelayed(() -> img_close.setAlpha(1.0f), 200);

            dialog.dismiss();
            finish();
        });

        String fullText = "We have sent you a 6-digit code to " + email + ".";
        SpannableString spannable = new SpannableString(fullText);
        // find where the email starts and ends
        int start = fullText.indexOf(email);
        int end = start + email.length();
        // bold style to the email part
        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        txt_otpDesc.setText(spannable);

        // dialog enter animation
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_enter);
        lineL_dialogOTP.startAnimation(animation);

        btn_verifyOTP.setOnClickListener(v -> {
            String otp = eTxt_otp.getText().toString().trim();
            if (otp.length() == 6) {
                // TODO: verify OTP
                Toast t = Toast.makeText(context, "OTP verified successfully!", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1200);
                dialog.dismiss();
            } else {
                eTxt_otp.setError("Enter valid OTP");
            }
        });

        txt_resendOTP.setOnClickListener(v -> {
            // animate
            txt_resendOTP.setAlpha(0.5f);
            new Handler().postDelayed(() -> txt_resendOTP.setAlpha(1.0f), 200);

            // resend
        });

        dialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private boolean validatePasswords() {
        if (isChange) {
            if (eTxt_oldPassword.getText().toString().isEmpty()) {
                eTxt_oldPassword.setError("Old password is required");
                return true;
            } // TODO: old password check

            eTxt_oldPassword.setError(null);
        }
        if (eTxt_password.getText().toString().isEmpty()) {
            eTxt_password.setError("Password is required");
            return true;
        } else if (Validators.isNotValidPassword(eTxt_password.getText().toString())) {
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