package com.example.quizflow.activities;

import android.content.Context;
import android.content.Intent;
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
import androidx.annotation.NonNull;
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
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.requests.RegisterRequest;
import com.example.quizflow.requests.ResendOtpRequest;
import com.example.quizflow.requests.ForgetPasswordRequest;
import com.example.quizflow.requests.VerifyOtpRequest;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {
    private ImageView img_odlEye;
    private ImageView img_eye;
    private ImageView img_retypeEye;
    private EditText eTxt_oldPassword, eTxt_password, eTxt_retypePassword;
    private TextView txt_decorPassword, txt_btnDone;

    private boolean isChange = false, isForget = false, isNew = false;
    private boolean isOldPasswordVisible = false, isPasswordVisible = false, isRetypePasswordVisible = false;
    private RegisterRequest user;

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

        // switch password action
        //isChange = getIntent().getBooleanExtra("isChange", false);
        isForget = getIntent().getBooleanExtra("isForget", false);
        isNew = getIntent().getBooleanExtra("isNew", false);

        initViews();
    }

    private void initViews() {
        // return action
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        // password inputs
        eTxt_oldPassword = findViewById(R.id.eTxt_oldPassword);
        eTxt_password = findViewById(R.id.eTxt_password);
        eTxt_retypePassword = findViewById(R.id.eTxt_retypePassword);

        // get email
        if (isNew) {
            user = (RegisterRequest) getIntent().getSerializableExtra("SIGNUP_USER");
        }
        if (isForget) {
            user = new RegisterRequest();
            user.setEmail(getIntent().getStringExtra("FORGET_EMAIL"));
        }

        if (user == null) {
            Utilities.showError(this, "QF_ERR_PASSWORD_USER", "Unable to obtain info");
            finish();
        } else {
            // show OTP dialog
            new Handler().postDelayed(() -> showOtpDialog(this), 1000);
        }

        // ui
        txt_decorPassword = findViewById(R.id.txt_decorPassword);
        if (isForget || isNew) {    // forget pwd
            if (isForget) txt_decorPassword.setText("Reset Your Password");
            if (isNew) txt_decorPassword.setText("Set Your Password");

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

            Retrofit2Client retrofit2Client = new Retrofit2Client();
            user.setPassword(eTxt_password.getText().toString().trim());
            if (isNew) {
                Call<ResponseBody> call = retrofit2Client.getAPI().signUp(user);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PasswordActivity.this, "You can sign in now", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordActivity.this, SigninActivity.class);
                            intent.putExtra("SIGNIN_EMAIL", user.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            String msg = "Unknown error";
                            if (response.errorBody() != null) {
                                try {
                                    msg = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_DONE_NEW", "Error: " + msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_DONE_NEW","Failure: " + t.getMessage());
                    }
                });
            }
            if (isForget) {
                retrofit2Client.getAPI().updatePassword(new ForgetPasswordRequest(user.getEmail(), user.getPassword())).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PasswordActivity.this, "You can sign in now", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordActivity.this, SigninActivity.class);
                            intent.putExtra("SIGNIN_EMAIL", user.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            String msg = "Unknown error";
                            if (response.errorBody() != null) {
                                try {
                                    msg = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_DONE_FORGET", "Error: " + msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_DONE_FORGET","Failure: " + t.getMessage());
                    }
                });
            }
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

        ImageView img_close = dialogView.findViewById(R.id.img_remove);
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

        String email = "your email";
        if (isForget) {
            email = user.getEmail();
        }
        if (isNew) {
            email = Objects.requireNonNull(user).getEmail();
        }
        String finalEmail = email;

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
            Retrofit2Client retrofit2Client = new Retrofit2Client();
            VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest(finalEmail, otp);
            Call<ResponseBody> call = retrofit2Client.getAPI().verifyOtp(verifyOtpRequest);
            call.enqueue(new retrofit2.Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast t = Toast.makeText(context, "Verified", Toast.LENGTH_SHORT);
                        t.show();
                        new Handler().postDelayed(t::cancel, 1000);

                        dialog.dismiss();
                    } else {
                        eTxt_otp.setError("OTP invalid");

                        String msg = "Unknown error";
                        if (response.errorBody() != null) {
                            try {
                                msg = response.errorBody().string();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_VERIFY", "Error: " + msg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_VERIFY","Failure: " + t.getMessage());
                }
            });
        });

        txt_resendOTP.setOnClickListener(v -> {
            // animate
            txt_resendOTP.setAlpha(0.5f);
            new Handler().postDelayed(() -> txt_resendOTP.setAlpha(1.0f), 200);

            // resend
            Retrofit2Client retrofit2Client = new Retrofit2Client();
            Call<ResponseBody> call = retrofit2Client.getAPI().resendOtp(new ResendOtpRequest(finalEmail));
            call.enqueue(new retrofit2.Callback<>() {
                 @Override
                 public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                     if (response.isSuccessful()) {
                         Toast t = Toast.makeText(context, "Please check your email!", Toast.LENGTH_SHORT);
                         t.show();
                         new Handler().postDelayed(t::cancel, 1200);
                     } else {
                         String msg = "Unknown error";
                         if (response.errorBody() != null) {
                             try {
                                 msg = response.errorBody().string();
                             } catch (IOException e) {
                                 throw new RuntimeException(e);
                             }
                         }
                         Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_RESEND", "Error: " + msg);
                     }
                 }

                 @Override
                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utilities.showError(PasswordActivity.this, "QF_ERR_PASSWORD_RESEND","Failure: " + t.getMessage());
                 }
            });
        });

        dialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        Objects.requireNonNull(dialog.getWindow()).setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private boolean validatePasswords() {
        if (isChange) {
            if (eTxt_oldPassword.getText().toString().isEmpty()) {
                eTxt_oldPassword.setError("Old password is required");
                return true;
            }

            eTxt_oldPassword.setError(null);
        }
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