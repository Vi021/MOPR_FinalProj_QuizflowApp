package com.example.quizflow.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText eTxt_username, eTxt_fullname, eTxt_email;
    private TextView txt_btnUploadAvatar, txt_btnUpdateProfile;
    private ImageView backButton;
    private Retrofit2Client retrofitClient;
    private Uri selectedImageUri;
    private AccountModel user;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri)
                            .placeholder(R.drawable.ic_default_pfp_icebear)
                            .error(R.drawable.ic_default_pfp_icebear)
                            .into(profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Status bar customization
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        retrofitClient = new Retrofit2Client();

        // Initialize views
        profileImage = findViewById(R.id.imageView7);
        eTxt_username = findViewById(R.id.eTxt_username);
        eTxt_fullname = findViewById(R.id.eTxt_fullname);
        eTxt_email = findViewById(R.id.eTxt_email);
        txt_btnUploadAvatar = findViewById(R.id.txt_btnUploadAvatar);
        txt_btnUpdateProfile = findViewById(R.id.txt_btnUpdateProfile);
        backButton = findViewById(R.id.imageView6);

        // Fetch user data from API
        Long uid = Utilities.getUID(this);
        if (uid != null) {
            Utilities.getUserByUidAsync(this, uid, new Utilities.AccountCallback() {
                @Override
                public void onSuccess(AccountModel account) {
                    user = account;
                    populateUserData();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Back button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Placeholder for Upload Avatar and Update Profile (to be implemented)
        txt_btnUploadAvatar.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+: Use READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
                } else {
                    openGallery();
                }
            } else {
                // Android 12 and below: Use READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                } else {
                    openGallery();
                }
            }
        });

        txt_btnUpdateProfile.setOnClickListener(v -> {
            String newUsername = eTxt_username.getText().toString().trim();
            String newFullname = eTxt_fullname.getText().toString().trim();
            String newEmail = eTxt_email.getText().toString().trim();

            if (newUsername.isEmpty()) {
                eTxt_username.setError("Username is required");
                return;
            }
            if (newFullname.isEmpty()) {
                eTxt_fullname.setError("Full name is required");
                return;
            }
            if (newEmail.isEmpty()) {
                eTxt_email.setError("Email is required");
                return;
            }

            updateProfile(newUsername, newFullname, newEmail, selectedImageUri);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void populateUserData() {
        eTxt_username.setText(user.getUsername());
        eTxt_fullname.setText(user.getFullname());
        eTxt_email.setText(user.getEmail());
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            String imageUrl = Refs.BASE_IMAGE_URL + user.getImage();
            Log.d("EditProfileActivity", "Loading image URL: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_pfp_icebear)
                    .error(R.drawable.ic_default_pfp_icebear)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_pfp_icebear);
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        // Create a temporary file in the cache directory
        String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getCacheDir(), fileName);

        // Copy the Uri content to the temporary file
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }

        return file;
    }

    private void updateProfile(String username, String fullname, String email, Uri imageUri) {
        txt_btnUpdateProfile.setEnabled(false);

        // Create request body for profile data
        RequestBody uidBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user.getUid()));
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody fullnameBody = RequestBody.create(MediaType.parse("text/plain"), fullname);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);

        // Handle image upload if selected
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            try {
                File file = getFileFromUri(imageUri);
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageBody);
            } catch (IOException e) {
                Log.e("EditProfileActivity", "Image processing error: " + e.getMessage());
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                txt_btnUpdateProfile.setEnabled(true);
                return;
            }
        }

        if (retrofitClient == null || retrofitClient.getAPI() == null) {
            Log.e("EditProfileActivity", "Retrofit client or API service is null");
            Toast.makeText(this, "Network configuration error", Toast.LENGTH_SHORT).show();
            txt_btnUpdateProfile.setEnabled(true);
            return;
        }

        // Make API call
        Call<Map<String, Object>> call = retrofitClient.getAPI().updateProfileWithUid(
                uidBody, usernameBody, fullnameBody, emailBody, imagePart);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                txt_btnUpdateProfile.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    String message = (String) response.body().get("message");
                    Toast.makeText(EditProfileActivity.this,
                            message != null ? message : "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    // Navigate back to AccountActivity
                    Intent intent = new Intent(EditProfileActivity.this, AccountActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Failed to update profile";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e("EditProfileActivity", "Error parsing error response", e);
                    }
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                txt_btnUpdateProfile.setEnabled(true);
                Log.e("EditProfileActivity", "Network error: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}