package com.example.quizflow.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.adapters.QuizAdapter;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recy_cateQuizzes;
    private TextView txt_none;
    private String category;

    private QuizAdapter quizAdapter;
    private List<QuizModel> quizzes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        initViews();
        getCategoryQuizzes();
    }

    private void initViews() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> finish());

        ImageView img_cateIcon = findViewById(R.id.img_cateIcon);

        TextView txt_cateName = findViewById(R.id.txt_cateName);
        category = getIntent().getStringExtra("category");
        if (category != null) {
            txt_cateName.setText(Utilities.toTitleCase(category));

            if (TYPE.TOPIC.get(category) != null) {
                img_cateIcon.setImageResource(TYPE.TOPIC.get(category));
            } else {
                img_cateIcon.setImageResource(R.drawable.ic_help_white);
                img_cateIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.xanh_ngoc)));
            }
        } else {
            txt_cateName.setText("Unknown Category");
            img_cateIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.xanh_ngoc)));
        }

        quizAdapter = new QuizAdapter(this, quizzes);
        recy_cateQuizzes = findViewById(R.id.recy_cateQuizzes);
        recy_cateQuizzes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recy_cateQuizzes.setAdapter(quizAdapter);
        txt_none = findViewById(R.id.txt_none);
        getCategoryQuizzes();

        CircleImageView cirImg_addQuiz = findViewById(R.id.cirImg_addQuiz);
        cirImg_addQuiz.setOnClickListener(v -> {
            QuizModel quiz = new QuizModel();
            quiz.setTopic(category);
            Intent intent = new Intent(this, QuizEditor2Activity.class);
            intent.putExtra("quiz", quiz);
            startActivity(intent);
        });
    }

    private void getCategoryQuizzes() {
        Retrofit2Client.getAPI().getPublicQuizzesByTopic(category).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizzes = response.body();

                    runOnUiThread(() -> loadQuizzes());
                } else {
                    String msg = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            msg = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Utilities.showError(TopicActivity.this, "QF_CAT_QUIZZES", "Error: " + msg);
                }
            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                Utilities.showError(TopicActivity.this, "QF_CAT_QUIZZES", "Failure: " + t.getMessage());
            }
        });
    }

    private void loadQuizzes() {
        if (quizzes == null || quizzes.isEmpty()) {
            txt_none.setVisibility(View.VISIBLE);
        } else {
            txt_none.setVisibility(View.GONE);
        }
        quizAdapter.setQuizzes(quizzes);
    }
}