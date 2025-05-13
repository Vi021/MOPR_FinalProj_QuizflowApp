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
import com.example.quizflow.adapters.QuizAdapter;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recy_cateQuizzes;
    private TextView txt_none;

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
    }

    private void initViews() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> finish());

        ImageView img_cateIcon = findViewById(R.id.img_cateIcon);

        TextView txt_cateName = findViewById(R.id.txt_cateName);
        String category = getIntent().getStringExtra("category");
        if (category != null) {
            txt_cateName.setText(Utilities.toUpperUnderscore(category));

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

        recy_cateQuizzes = findViewById(R.id.recy_cateQuizzes);
        txt_none = findViewById(R.id.txt_none);
        getCategoryQuizzes();

        CircleImageView cirImg_addQuiz = findViewById(R.id.cirImg_addQuiz);
        cirImg_addQuiz.setOnClickListener(v -> {
            QuizModel quiz = new QuizModel();
            quiz.setTopic(Utilities.toUpperUnderscore(txt_cateName.getText().toString()));
            Intent intent = new Intent(this, QuizEditorActivity.class);
            intent.putExtra("quiz", quiz);
            startActivity(intent);
        });
    }

    private void getCategoryQuizzes() {
        // TODO: load when getting quizzes by category
        //       if empty, display text

        List<QuizModel> artQuizzes = new ArrayList<>();

        artQuizzes.add(new QuizModel(
                1L,
                "Famous Paintings Quiz",
                "Test your knowledge of famous paintings and their artists.",
                "ART",
                true,
                "2025-05-01 14:00:00",
                10,
                600,
                101L,
                25,
                (byte) 1
        ));

        artQuizzes.add(new QuizModel(
                2L,
                "Modern Art Masterpieces",
                "A quiz on key figures and works in modern art.",
                "ART",
                true,
                "2025-04-28 09:30:00",
                8,
                480,
                102L,
                15,
                (byte) 4
        ));

        artQuizzes.add(new QuizModel(
                3L,
                "Art History Basics",
                "Covers art history from ancient times to the Renaissance.",
                "ART",
                false,
                "2025-05-02 18:45:00",
                12,
                900,
                103L,
                5,
                (byte) 7
        ));

        artQuizzes.add(new QuizModel(
                4L,
                "Sculpture & Statues",
                "Identify famous sculptures and their creators.",
                "ART",
                true,
                "2025-04-25 12:20:00",
                7,
                420,
                104L,
                9,
                (byte) 2
        ));

        recy_cateQuizzes.setAdapter(new QuizAdapter(this, artQuizzes));
        recy_cateQuizzes.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recy_cateQuizzes.setHasFixedSize(true);
        txt_none.setVisibility(View.GONE);
    }
}