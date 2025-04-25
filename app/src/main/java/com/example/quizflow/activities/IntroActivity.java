package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizflow.R;

import java.util.Random;

public class IntroActivity extends AppCompatActivity {
    TextView txt_intros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_intros = findViewById(R.id.txt_intros);
        setIntros();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(IntroActivity.this, SigninActivity.class));
            finish();
        }, 2000);
    }

    private void setIntros() {
        String[] intros = {
                "Welcome!",
                "Getting things ready...",
                "Loading questions...",
                "Ready to challenge your brain?",
                "Test your knowledge!",
                "Let’s get started!",
                "Think fast, answer faster!",
                "Sharpen your brain!",
                "Are you ready to quiz?",
                "Prepare for a challenge!",
                "Knowledge is power!",
                "Unlock your potential!",
                "Expand your mind!",
                "Quiz time!",
                "Get set for a quiz!",
        };

        int length = intros.length;
        Random random = new Random();
        Handler introMessageHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int index = random.nextInt(length);
                txt_intros.setText(intros[index]);

                introMessageHandler.postDelayed(this, 200);
            }
        };

        introMessageHandler.post(runnable);
    }
}