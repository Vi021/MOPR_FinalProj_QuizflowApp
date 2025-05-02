package com.example.quizflow.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Validators;

import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recy_cateQuizzes;
    private TextView txt_none;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
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

        TextView txt_cateName = findViewById(R.id.txt_cateName);
        String category = getIntent().getStringExtra("category");
        if (category != null) {
            txt_cateName.setText(Validators.toUpperUnderscore(category));
        } else {
            txt_cateName.setText("Unknown Category");
        }

        ImageView img_cateIcon = findViewById(R.id.img_cateIcon);
        img_cateIcon.setImageResource(Objects.requireNonNullElseGet(TYPE.CATEGORY.get(category), () -> R.drawable.ic_help_white));

        recy_cateQuizzes = findViewById(R.id.recy_cateQuizzes);
        txt_none = findViewById(R.id.txt_none);
        getCategoryQuizzes();
    }

    private void getCategoryQuizzes() {
        // TODO: load when getting quizzes by category
        //       if empty, display text
    }
}