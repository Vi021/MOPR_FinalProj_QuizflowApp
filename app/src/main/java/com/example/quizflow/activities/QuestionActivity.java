package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.quizflow.models.QuestionsModel;
import com.example.quizflow.respones.AnswerResponse;
import com.example.quizflow.respones.QuestionResponse;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements QuestionAdapter.Score {
    private static final String TAG = "QuestionActivity";
    private ActivityQuestionBinding binding;
    private int position = 0;
    private List<QuestionsModel> questionList = new ArrayList<>();
    private int allScore = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Player> playerList;
    private boolean isMultiPlayer = false;
    private long qid;
    private List<String> selectedAnswers = new ArrayList<>();
    private long quizDuration = 600; // Default duration, will be updated from API

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

        Intent intent = getIntent();
        qid = intent.getLongExtra("qid", -1);
        isMultiPlayer = intent.getBooleanExtra("isMultiPlayer", false);

        if (qid == -1) {
            Toast.makeText(this, "Invalid quiz ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch quiz from backend
        Utilities.getQuizByIdAsync(this, qid, new Utilities.QuizCallback() {
            @Override
            public void onSuccess(QuizResponse quiz) {
                Log.d(TAG, "QuizResponse: qid=" + quiz.getQid() + ", title=" + quiz.getTitle());
                quizDuration = quiz.getDuration(); // Store the quiz duration
                Log.d(TAG, "Quiz duration: " + quizDuration + " seconds");
                
                for (QuestionResponse q : quiz.getQuestions()) {
                    Log.d(TAG, "Question qtid=" + q.getQtid() + ", text=" + q.getQuestion());
                    for (AnswerResponse a : q.getAnswers()) {
                        Log.d(TAG, "Answer aid=" + a.getAid() + ", text=" + a.getText() + ", isCorrect=" + a.isCorrect());
                    }
                }
                questionList = mapQuizToQuestionModel(quiz);
                if (questionList.isEmpty()) {
                    Toast.makeText(QuestionActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                selectedAnswers = new ArrayList<>(Collections.nCopies(questionList.size(), null));
                for (QuestionsModel q : questionList) {
                    Log.d(TAG, "QuestionsModel qtid=" + q.getQtid() + ", correctAnswer=" + q.getCorrectAnswer() + ", aids=" + Arrays.toString(q.getAids()));
                }
                initializeUI();
            }

            @Override
            public void onFailure(String error) {
                Utilities.showError(QuestionActivity.this, "QuestionActivity", error);
                finish();
            }
        });
    }
    private void initializeUI() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.progressBar.setProgress(1);
        binding.questionNumberTxt.setText("Question 1/" + questionList.size());
        binding.questionTxt.setText(questionList.get(position).getQuestion());
        binding.txtCurrentScore.setText("Score: 0");

        if (isMultiPlayer) {
            binding.txtRankingPosition.setVisibility(View.VISIBLE);
            playerList = initializePlayerList();
            updateRankingPosition();
        } else {
            binding.txtRankingPosition.setVisibility(View.GONE);
        }

        loadAnswers();
        Log.d(TAG, "Loaded question position=" + position + ", correctAnswer=" + questionList.get(position).getCorrectAnswer());

        binding.rightArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == questionList.size()) {
                proceedToScoreActivity();
                return;
            }

            position++;
            binding.progressBar.setProgress(binding.progressBar.getProgress() + 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/" + questionList.size());
            binding.questionTxt.setText(questionList.get(position).getQuestion());
            loadAnswers();
        });

        binding.leftArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == 1) return;

            position--;
            binding.progressBar.setProgress(binding.progressBar.getProgress() - 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/" + questionList.size());
            binding.questionTxt.setText(questionList.get(position).getQuestion());
            loadAnswers();
        });
    }

    private List<QuestionsModel> mapQuizToQuestionModel(QuizResponse quiz) {
        List<QuestionsModel> questions = new ArrayList<>();
        int index = 1;
        for (QuestionResponse q : quiz.getQuestions()) {
            String correctAnswer = null;
            String answer1 = null, answer2 = null, answer3 = null, answer4 = null;
            String aid1 = null, aid2 = null, aid3 = null, aid4 = null;
            int answerIndex = 1;
            for (AnswerResponse a : q.getAnswers()) {
                if (a.isCorrect()) {
                    correctAnswer = String.valueOf(a.getAid());
                    Log.d(TAG, "Set correctAnswer for qtid=" + q.getQtid() + ": aid=" + a.getAid());
                }
                if (answerIndex == 1) {
                    answer1 = a.getText();
                    aid1 = String.valueOf(a.getAid());
                } else if (answerIndex == 2) {
                    answer2 = a.getText();
                    aid2 = String.valueOf(a.getAid());
                } else if (answerIndex == 3) {
                    answer3 = a.getText();
                    aid3 = String.valueOf(a.getAid());
                } else if (answerIndex == 4) {
                    answer4 = a.getText();
                    aid4 = String.valueOf(a.getAid());
                }
                answerIndex++;
            }
            QuestionsModel question = new QuestionsModel(
                    q.getQtid(),
                    q.getQuestion(),
                    answer1,
                    answer2,
                    answer3,
                    answer4,
                    correctAnswer,
                    5,
                    "q_" + index,
                    null,
                    new String[]{aid1, aid2, aid3, aid4}
            );
            questions.add(question);
            Log.d(TAG, "Created QuestionsModel qtid=" + q.getQtid() + ", correctAnswer=" + correctAnswer +
                    ", answers=[" + answer1 + ", " + answer2 + ", " + answer3 + ", " + answer4 + "]" +
                    ", aids=[" + aid1 + ", " + aid2 + ", " + aid3 + ", " + aid4 + "]");
            index++;
        }
        return questions;
    }
    private void loadAnswers() {
        QuestionsModel question = questionList.get(position);
        List<String> answers = new ArrayList<>();
        List<String> aids = new ArrayList<>();
        if (question.getAnswer1() != null) {
            answers.add(question.getAnswer1());
            aids.add(question.getAids()[0]);
        }
        if (question.getAnswer2() != null) {
            answers.add(question.getAnswer2());
            aids.add(question.getAids()[1]);
        }
        if (question.getAnswer3() != null) {
            answers.add(question.getAnswer3());
            aids.add(question.getAids()[2]);
        }
        if (question.getAnswer4() != null) {
            answers.add(question.getAnswer4());
            aids.add(question.getAids()[3]);
        }

        Log.d(TAG, "Loading answers for position=" + position + ", answers=" + answers + ", aids=" + aids +
                ", correctAnswer=" + question.getCorrectAnswer());

        QuestionAdapter questionAdapter = new QuestionAdapter(
                question.getCorrectAnswer(),
                answers,
                aids,
                this
        );

        questionAdapter.differ.submitList(answers);
        binding.questionList.setLayoutManager(new LinearLayoutManager(this));
        binding.questionList.setAdapter(questionAdapter);
    }

    @Override
    public void amount(int number, String clickedAnswer) {
        allScore += number;
        selectedAnswers.set(position, clickedAnswer);
        questionList.get(position).setClickedAnswer(clickedAnswer);
        binding.txtCurrentScore.setText("Score: " + allScore);
        Log.d(TAG, "Selected answer position=" + position + ", clickedAnswer=" + clickedAnswer + ", scoreAdded=" + number);

        if (isMultiPlayer) {
            updatePlayerList(number);
            updateRankingPosition();
        }

        handler.postDelayed(() -> {
            if (binding.progressBar.getProgress() == questionList.size()) {
                proceedToScoreActivity();
            } else {
                position++;
                binding.progressBar.setProgress(binding.progressBar.getProgress() + 1);
                binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/" + questionList.size());
                binding.questionTxt.setText(questionList.get(position).getQuestion());
                loadAnswers();
            }
        }, 1500);
    }

    private void proceedToScoreActivity() {
        Intent scoreIntent = new Intent(QuestionActivity.this, ScoreActivity.class);
        scoreIntent.putExtra("score", allScore);
        scoreIntent.putExtra("qid", qid);
        scoreIntent.putExtra("duration", quizDuration); // Pass the stored quiz duration
        scoreIntent.putParcelableArrayListExtra("questions", new ArrayList<>(questionList));
        scoreIntent.putStringArrayListExtra("selectedAnswers", new ArrayList<>(selectedAnswers));
        startActivity(scoreIntent);
        finish();
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
