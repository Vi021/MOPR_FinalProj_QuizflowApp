package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.quizflow.R;
import com.example.quizflow.models.QuestionsModel;
import com.example.quizflow.requests.CoinHistoryRequest;
import com.example.quizflow.requests.QuizResponseRequest;
import com.example.quizflow.respones.AttemptRequest;
import com.example.quizflow.respones.AttemptResponse;
import com.example.quizflow.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {
    private static final String TAG = "ScoreActivity";
    private int score = 0;
    private long qid;
    private List<QuestionsModel> questions;
    private List<String> selectedAnswers;
    private Long uid;
    private long quizDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);

        // Cho phép tràn full màn hình
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Áp dụng insets để tránh che UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        qid = intent.getLongExtra("qid", -1);
        questions = intent.getParcelableArrayListExtra("questions");
        selectedAnswers = intent.getStringArrayListExtra("selectedAnswers");
        quizDuration = intent.getLongExtra("duration", 600); // Default to 600 if not found

        uid = Utilities.getUID(this);
        if (uid == null) {
            Toast.makeText(this, "Please sign in to save score", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị điểm
        TextView scoreTxt = findViewById(R.id.scoreTxt);
        scoreTxt.setText(String.valueOf(score));

        if (qid != -1 && questions != null && selectedAnswers != null) {
            saveQuizResults();
        }

        // Xử lý nút back về MainActivity
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            Intent mainIntent = new Intent(ScoreActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }
    private void saveQuizResults() {
        // Create attempt
        AttemptRequest attempt = new AttemptRequest();
        attempt.setScore(score);
        attempt.setSubmitTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()));
        attempt.setAttemptTime((int) quizDuration); // Cast to int since AttemptRequest expects an int
        attempt.setUid(uid);
        attempt.setQid(qid);

        Utilities.createAttemptAsync(this, attempt, new Utilities.AttemptCallback() {
            @Override
            public void onSuccess(AttemptResponse attemptResponse) {
                long atid = attemptResponse.getAtid();
                saveQuizResponses(atid);
                int coinsEarned = score; // 1 coin per 10 points
                if (coinsEarned > 0) {
                    updateCoinsAndHistory(coinsEarned);
                }
            }

            @Override
            public void onFailure(String error) {
                Utilities.showError(ScoreActivity.this, "ScoreActivity", "Failed to save attempt: " + error);
            }
        });
    }

    private void saveQuizResponses(long atid) {
        for (int i = 0; i < questions.size(); i++) {
            String selectedAnswer = selectedAnswers.get(i);
            if (selectedAnswer != null) {
                QuizResponseRequest response = new QuizResponseRequest();
                response.setAtid(atid);
                response.setQtid(questions.get(i).getQtid());
                response.setAnswer(selectedAnswer);

                Utilities.createQuizResponseAsync(this, response, new Utilities.GenericCallback() {
                    @Override
                    public void onSuccess() {
                        // Response saved
                    }

                    @Override
                    public void onFailure(String error) {
                        Utilities.showError(ScoreActivity.this, "ScoreActivity", "Failed to save response: " + error);
                    }
                });
            }
        }
    }

    private void updateCoinsAndHistory(int coins) {
        // Chỉ gọi createCoinHistory trực tiếp, không gọi updateUserCoins
        CoinHistoryRequest history = new CoinHistoryRequest();
        history.setUid(uid);
        history.setCoins(coins);
        history.setDescription("Earned from quiz " + qid);
        String transactionTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
        history.setTransactionTime(transactionTime);

        Utilities.createCoinHistoryAsync(ScoreActivity.this, history, new Utilities.GenericCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ScoreActivity.this, "Earned " + coins + " coins!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Utilities.showError(ScoreActivity.this, "ScoreActivity", "Failed to save coin history: " + error);
            }
        });
    }
}
