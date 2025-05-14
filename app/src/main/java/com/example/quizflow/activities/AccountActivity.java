package com.example.quizflow.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.respones.UserResponse;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.Utilities;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    LinearLayout editProfileLayout, myFavoriteLayout, myQuizLayout, changePasswordLayout;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        // Initialize views
        profileImage = findViewById(R.id.imageView7);
        editProfileLayout = findViewById(R.id.linearLayoutEditProfile);
        myFavoriteLayout = findViewById(R.id.myFavoriteLayout);
        myQuizLayout = findViewById(R.id.myQuizLayout);
        changePasswordLayout = findViewById(R.id.linearChangePassword);
        ImageView backButton = findViewById(R.id.imageView6);

        // Load user data from API
        Long uid = Utilities.getUID(this);
        if (uid != null) {
            Utilities.getUserByUidAsync(this, uid, new Utilities.AccountCallback() {
                @Override
                public void onSuccess(AccountModel user) {
                    loadUserData(user);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(AccountActivity.this, error, Toast.LENGTH_SHORT).show();
                    profileImage.setImageResource(R.drawable.ic_default_pfp_icebear);
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            profileImage.setImageResource(R.drawable.ic_default_pfp_icebear);
            finish();
        }

        // Set click listeners
        editProfileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        myFavoriteLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FTAG", "COLLECTION");
            startActivity(intent);
            finish();
        });

        myQuizLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FTAG", "COLLECTION");
            startActivity(intent);
            finish();
        });

        changePasswordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadUserData(AccountModel user) {
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            String imageUrl = Refs.BASE_IMAGE_URL + user.getImage();
            Log.d("AccountActivity", "Loading image URL: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_pfp_icebear)
                    .error(R.drawable.ic_default_pfp_icebear)
                    .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("AccountActivity", "Glide load failed: " + (e != null ? e.getMessage() : "unknown error"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_pfp_icebear);
        }
    }
}
