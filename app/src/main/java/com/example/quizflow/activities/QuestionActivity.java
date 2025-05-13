package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizflow.R;
import com.example.quizflow.adapters.QuestionAdapter;
import com.example.quizflow.databinding.ActivityQuestionBinding;
import com.example.quizflow.QuestionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements QuestionAdapter.Score {

    private ActivityQuestionBinding binding;
    private int position = 0;
    private List<QuestionModel> receivedList = new ArrayList<>();
    private int allScore = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Player> playerList;
    private boolean isMultiPlayer = false;

    private static class Player {
        private String name;
        private int score;

        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set padding for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Nhận quizCode và danh sách câu hỏi từ Intent
        Intent intent = getIntent();
        String quizCode = intent.getStringExtra("quizCode");
        isMultiPlayer = intent.getBooleanExtra("isMultiPlayer", false);
        receivedList = intent.getParcelableArrayListExtra("list");

        if (quizCode != null) {
            Toast.makeText(this, "Quiz Code: " + quizCode, Toast.LENGTH_SHORT).show();
        }

        if (receivedList == null || receivedList.isEmpty()) {
            receivedList = new ArrayList<>();
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo giao diện
        binding.backBtn.setOnClickListener(v -> finish());
        binding.progressBar.setProgress(1);
        binding.questionNumberTxt.setText("Question 1/10");
        binding.questionTxt.setText(receivedList.get(position).getQuestion());
        binding.txtCurrentScore.setText("Score: 0");

        // Hiển thị hoặc ẩn txt_ranking_position
        if (isMultiPlayer) {
            binding.txtRankingPosition.setVisibility(View.VISIBLE);
            playerList = initializePlayerList();
            updateRankingPosition();
        } else {
            binding.txtRankingPosition.setVisibility(View.GONE);
        }

        loadAnswers();

        // Nút Right Arrow
        binding.rightArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == 10) {
                Intent scoreIntent = new Intent(QuestionActivity.this, ScoreActivity.class);
                scoreIntent.putExtra("Score", allScore);
                startActivity(scoreIntent);
                finish();
                return;
            }

            position++;
            binding.progressBar.setProgress(binding.progressBar.getProgress() + 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/10");
            binding.questionTxt.setText(receivedList.get(position).getQuestion());

            loadAnswers();
        });

        binding.leftArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == 1) return;

            position--;
            binding.progressBar.setProgress(binding.progressBar.getProgress() - 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/10");
            binding.questionTxt.setText(receivedList.get(position).getQuestion());
            loadAnswers();
        });
    }

    private void loadAnswers() {
        List<String> users = new ArrayList<>();
        users.add(String.valueOf(receivedList.get(position).getAnswer1()));
        users.add(String.valueOf(receivedList.get(position).getAnswer2()));
        users.add(String.valueOf(receivedList.get(position).getAnswer3()));
        users.add(String.valueOf(receivedList.get(position).getAnswer4()));

        if (receivedList.get(position).getClickedAnswer() != null) {
            users.add(String.valueOf(receivedList.get(position).getClickedAnswer()));
        }

        QuestionAdapter questionAdapter = new QuestionAdapter(
                String.valueOf(receivedList.get(position).getCorrectAnswer()),
                users,
                this
        );

        questionAdapter.differ.submitList(users);
        binding.questionList.setLayoutManager(new LinearLayoutManager(this));
        binding.questionList.setAdapter(questionAdapter);
    }

    @Override
    public void amount(int number, String clickedAnswer) {
        allScore += number;
        receivedList.get(position).setClickedAnswer(clickedAnswer);
        binding.txtCurrentScore.setText("Score: " + allScore);

        // Cập nhật xếp hạng giả lập (cho Multi Player)
        if (isMultiPlayer) {
            updatePlayerList(number);
            updateRankingPosition();
        }

        // Trì hoãn 3 giây trước khi chuyển câu hỏi
        handler.postDelayed(() -> {
            if (binding.progressBar.getProgress() == 10) {
                Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
                intent.putExtra("Score", allScore);
                startActivity(intent);
                finish();
            } else {
                position++;
                binding.progressBar.setProgress(binding.progressBar.getProgress() + 1);
                binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/10");
                binding.questionTxt.setText(receivedList.get(position).getQuestion());
                loadAnswers();
            }
        }, 1500); // 1.5 giây
    }

    // Giả lập danh sách người chơi
    private List<Player> initializePlayerList() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("You", allScore));
        players.add(new Player("Player 2", 0));
        players.add(new Player("Player 3", 0));
        players.add(new Player("Player 4", 0));
        return players;
    }

    // Cập nhật điểm giả lập cho người chơi
    private void updatePlayerList(int yourScore) {
        Random random = new Random();
        playerList.get(0).setScore(allScore); // Cập nhật điểm của bạn
        for (int i = 1; i < playerList.size(); i++) {
            // Giả lập điểm ngẫu nhiên cho người chơi khác (0-5 điểm mỗi câu)
            int currentScore = playerList.get(i).getScore();
            playerList.get(i).setScore(currentScore + random.nextInt(6));
        }
        // Sắp xếp theo điểm giảm dần
        Collections.sort(playerList, (p1, p2) -> p2.getScore() - p1.getScore());
    }

    // Cập nhật số thứ tự xếp hạng
    private void updateRankingPosition() {
        int rank = 1;
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getName().equals("You")) {
                rank = i + 1;
                break;
            }
        }
        binding.txtRankingPosition.setText("Rank: " + rank);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Xóa tất cả callback để tránh memory leak
    }
}
