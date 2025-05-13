package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizflow.QuestionModel;
import com.example.quizflow.R;

import java.util.ArrayList;
import java.util.List;

public class WaitingActivity extends AppCompatActivity {
    private TextView txtQuizCode, txtParticipants;
    private Button btnStart, btnCancel;
    private int participantCount = 1; // Giả lập số người tham gia
    private boolean isHost = true; // Giả lập người đầu tiên là host
    private final int MAX_PARTICIPANTS = 4;
    private Handler handler = new Handler();
    private Runnable participantUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        txtQuizCode = findViewById(R.id.txt_quizCode);
        txtParticipants = findViewById(R.id.txt_participants);
        btnStart = findViewById(R.id.btn_start);
        btnCancel = findViewById(R.id.btn_cancel);

        // Lấy quizCode từ Intent
        String quizCode = getIntent().getStringExtra("quizCode");
        if (quizCode != null) {
            txtQuizCode.setText("Quiz Code: " + quizCode);
        } else {
            txtQuizCode.setText("Quiz Code: N/A");
        }

        // Hiển thị số người tham gia
        updateParticipantCount();

        // Hiển thị nút Start cho host
        if (isHost) {
            btnStart.setVisibility(View.VISIBLE);
        }

        // Giả lập cập nhật số người tham gia (thay bằng WebSocket/API thực tế)
        participantUpdater = new Runnable() {
            @Override
            public void run() {
                if (participantCount < MAX_PARTICIPANTS) {
                    participantCount++;
                    updateParticipantCount();
                    handler.postDelayed(this, 2000); // Cập nhật mỗi 2s
                }
            }
        };
        handler.postDelayed(participantUpdater, 2000);

        // Xử lý nút Start (chỉ host)
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingActivity.this, QuestionActivity.class);
            intent.putExtra("quizCode", quizCode);
            intent.putParcelableArrayListExtra("list", new ArrayList<>(questionList()));
            intent.putExtra("isMultiPlayer", true); // Xác định Multi Player
            startActivity(intent);
            finish(); // Kết thúc WaitingActivity
            // TODO: Thông báo cho các người chơi khác qua WebSocket/API để chuyển sang QuestionActivity
        });

        // Xử lý nút Cancel
        btnCancel.setOnClickListener(v -> {
            handler.removeCallbacks(participantUpdater); // Dừng giả lập
            finish();
        });
    }

    private void updateParticipantCount() {
        txtParticipants.setText("Participants: " + participantCount + "/" + MAX_PARTICIPANTS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(participantUpdater); // Dừng giả lập khi Activity hủy
    }

    // Danh sách câu hỏi mẫu
    private List<QuestionModel> questionList() {
        List<QuestionModel> questions = new ArrayList<>();

        questions.add(new QuestionModel(
                1,
                "Which planet is the largest planet in the solar system?",
                "Earth",
                "Mars",
                "Neptune",
                "Jupiter",
                "d",
                5,
                "q_1",
                null
        ));

        questions.add(new QuestionModel(
                2,
                "Which country is the largest country in the world by land area?",
                "Russia",
                "Canada",
                "United States",
                "China",
                "a",
                5,
                "q_2",
                null
        ));

        questions.add(new QuestionModel(
                3,
                "Which of the following substances is used as an anti-cancer medication in the medical world?",
                "Cheese",
                "Lemon juice",
                "Cannabis",
                "Paspalum",
                "c",
                5,
                "q_3",
                null
        ));

        questions.add(new QuestionModel(
                4,
                "Which moon in the Earth's solar system has an atmosphere?",
                "Luna (Moon)",
                "Phobos (Mars' moon)",
                "Venus' moon",
                "None of the above",
                "d",
                5,
                "q_4",
                null
        ));

        questions.add(new QuestionModel(
                5,
                "Which of the following symbols represents the chemical element with the atomic number 6?",
                "O",
                "H",
                "C",
                "N",
                "c",
                5,
                "q_5",
                null
        ));

        questions.add(new QuestionModel(
                6,
                "Who is credited with inventing theater as we know it today?",
                "Shakespeare",
                "Arthur Miller",
                "Ashkouri",
                "Ancient Greeks",
                "d",
                5,
                "q_6",
                null
        ));

        questions.add(new QuestionModel(
                7,
                "Which ocean is the largest ocean in the world?",
                "Pacific Ocean",
                "Atlantic Ocean",
                "Indian Ocean",
                "Arctic Ocean",
                "a",
                5,
                "q_7",
                null
        ));

        questions.add(new QuestionModel(
                8,
                "Which religions are among the most practiced religions in the world?",
                "Islam, Christianity, Judaism",
                "Buddhism, Hinduism, Sikhism",
                "Zoroastrianism, Brahmanism, Yazdanism",
                "Taoism, Shintoism, Confucianism",
                "a",
                5,
                "q_8",
                null
        ));

        questions.add(new QuestionModel(
                9,
                "In which continent are the most independent countries located?",
                "Asia",
                "Europe",
                "Africa",
                "Americas",
                "c",
                5,
                "q_9",
                null
        ));

        questions.add(new QuestionModel(
                10,
                "Which ocean has the greatest average depth?",
                "Pacific Ocean",
                "Atlantic Ocean",
                "Indian Ocean",
                "Southern Ocean",
                "d",
                5,
                "q_10",
                null
        ));

        return questions;
    }
}
