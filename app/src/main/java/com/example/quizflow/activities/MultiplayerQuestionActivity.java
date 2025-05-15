package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizflow.APIService;
import com.example.quizflow.R;
import com.example.quizflow.adapters.QuestionAdapter;
import com.example.quizflow.databinding.ActivityMultiplayerQuestionBinding;
import com.example.quizflow.models.LobbyStatus;
import com.example.quizflow.models.ParticipantStatus;
import com.example.quizflow.models.QuestionsModel;
import com.example.quizflow.requests.QuizAnswerRequest;
import com.example.quizflow.respones.AnswerResponse;
import com.example.quizflow.respones.QuestionResponse;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.utils.Refs;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MultiplayerQuestionActivity extends AppCompatActivity implements QuestionAdapter.Score {
    private static final String TAG = "MultiplayerQuestionActivity";
    private ActivityMultiplayerQuestionBinding binding;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private Long lid, uid, qid;
    private List<QuestionsModel> questionList = new ArrayList<>();
    private List<String> selectedAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int allScore = 0;
    private CountDownTimer timer;
    private long questionTime = 30000; // Giá trị mặc định: 30 giây mỗi câu hỏi 
    private long quizDuration = 600; // Default duration in seconds
    private APIService apiService;
    private boolean isHost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMultiplayerQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Refs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

        Intent intent = getIntent();
        lid = intent.getLongExtra("lid", -1);
        uid = intent.getLongExtra("uid", -1);
        qid = intent.getLongExtra("qid", -1);
        isHost = intent.getBooleanExtra("isHost", false);

        Log.d(TAG, "Started with lid=" + lid + ", uid=" + uid + ", qid=" + qid + 
              ", isHost=" + isHost);

        if (lid == -1 || uid == -1 || qid == -1) {
            Toast.makeText(this, "Invalid game data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.modeTxt.setText("Multi Player");
        binding.backBtn.setOnClickListener(v -> finish());

        // Set up initial UI
        binding.txtCurrentScore.setText("Score: 0");
        binding.txtRankingPosition.setText("Rank: -");

        // Lấy danh sách câu hỏi từ API
        fetchQuizQuestions();
        
        // Kết nối tới STOMP WebSocket
        connectStompClient();
    }

    private void fetchQuizQuestions() {
        Call<QuizResponse> call = apiService.getQuizById(qid);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuizResponse quiz = response.body();
                    quizDuration = quiz.getDuration(); // Store the quiz duration
                    Log.d(TAG, "Quiz duration: " + quizDuration + "s");
                    
                    for (QuestionResponse question : quiz.getQuestions()) {
                        addQuestion(question);
                    }
                    
                    // Tính thời gian cho mỗi câu hỏi dựa trên duration của quiz
                    if (quiz.getDuration() > 0 && !questionList.isEmpty()) {
                        // Duration từ server là giây, cần chuyển thành milliseconds
                        long totalTimeInMillis = quiz.getDuration() * 1000;
                        questionTime = totalTimeInMillis / questionList.size();
                        Log.d(TAG, "Quiz duration: " + quiz.getDuration() + "s, Questions: " + 
                               questionList.size() + ", Time per question: " + (questionTime/1000) + "s");
                    }
                    
                    if (!questionList.isEmpty()) {
                        displayQuestion();
                        startTimer();
                    }
                } else {
                    Toast.makeText(MultiplayerQuestionActivity.this, "Failed to load quiz", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching quiz: " + t.getMessage());
                Toast.makeText(MultiplayerQuestionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void connectStompClient() {
        String wsUrl = "ws://" + Refs.BASE_IP + ":" + Refs.BASE_PORT + "/ws";
        Log.d(TAG, "Connecting to STOMP server: " + wsUrl);
        
        compositeDisposable = new CompositeDisposable();
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        
        stompClient.withClientHeartbeat(15000).withServerHeartbeat(15000);
        
        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "STOMP connection opened");
                            break;
                        case CLOSED:
                            Log.d(TAG, "STOMP connection closed");
                            break;
                        case ERROR:
                            Log.e(TAG, "STOMP connection error", lifecycleEvent.getException());
                            Toast.makeText(MultiplayerQuestionActivity.this, 
                                    "Connection error: " + lifecycleEvent.getException().getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
        
        compositeDisposable.add(lifecycleDisposable);
        
        // Subscribe to lobby updates
        Disposable topicDisposable = stompClient.topic("/topic/lobby/" + lid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "STOMP message received: " + topicMessage.getPayload());
                    
                    try {
                        Gson gson = new Gson();
                        LobbyStatus status = gson.fromJson(topicMessage.getPayload(), LobbyStatus.class);
                        
                        if (status != null && status.getLid() != null && status.getLid().equals(lid)) {
                            Log.d(TAG, "Received status update for our lobby. Status: " + status.getStatus() + 
                                  ", Current question index: " + status.getCurrentQuestionIndex() + 
                                  ", Current index in app: " + currentQuestionIndex);
                            
                            updateRanking(status);
                            
                            if ("FINISHED".equals(status.getStatus())) {
                                Log.d(TAG, "Game finished, proceeding to score activity");
                                proceedToScoreActivity();
                            } else if (status.getCurrentQuestionIndex() != null && 
                                    status.getCurrentQuestionIndex() > currentQuestionIndex) {
                                Log.d(TAG, "Moving to next question. New index: " + status.getCurrentQuestionIndex());
                                currentQuestionIndex = status.getCurrentQuestionIndex();
                                displayQuestion();
                                startTimer();
                            }
                        } else {
                            Log.d(TAG, "Received message for different lobby or null status");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing STOMP message: " + e.getMessage(), e);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        
        compositeDisposable.add(topicDisposable);
        
        stompClient.connect();
    }

    private void addQuestion(QuestionResponse question) {
        String correctAnswer = null;
        String answer1 = null, answer2 = null, answer3 = null, answer4 = null;
        String aid1 = null, aid2 = null, aid3 = null, aid4 = null;
        int answerIndex = 1;
        for (AnswerResponse a : question.getAnswers()) {
            if (a.isCorrect()) {
                correctAnswer = String.valueOf(a.getAid());
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
        QuestionsModel model = new QuestionsModel(
                question.getQtid(),
                question.getQuestion(),
                answer1,
                answer2,
                answer3,
                answer4,
                correctAnswer,
                10,
                "q_" + (questionList.size() + 1),
                null,
                new String[]{aid1, aid2, aid3, aid4}
        );
        questionList.add(model);
        selectedAnswers.add(null);
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questionList.size()) return;
        QuestionsModel question = questionList.get(currentQuestionIndex);
        binding.questionNumberTxt.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
        binding.questionTxt.setText(question.getQuestion());
        binding.progressBar.setProgress(currentQuestionIndex + 1);

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

        QuestionAdapter adapter = new QuestionAdapter(
                question.getCorrectAnswer(),
                answers,
                aids,
                this
        );
        adapter.differ.submitList(answers);
        binding.questionList.setLayoutManager(new LinearLayoutManager(this));
        binding.questionList.setAdapter(adapter);
        binding.questionList.setEnabled(true);
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(questionTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText("Time: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                binding.txtTimer.setText("Time: 0s");
                binding.questionList.setEnabled(false);
                // Gửi câu trả lời mặc định (null) nếu hết thời gian
                if (selectedAnswers.get(currentQuestionIndex) == null) {
                    sendAnswer(null);
                }
            }
        }.start();
    }

    @Override
    public void amount(int number, String clickedAnswer) {
        binding.questionList.setEnabled(false);
        selectedAnswers.set(currentQuestionIndex, clickedAnswer);
        sendAnswer(clickedAnswer);
    }

    private void sendAnswer(String clickedAnswer) {
        QuizAnswerRequest request = new QuizAnswerRequest();
        request.setLid(lid);
        request.setUid(uid);
        request.setQtid(questionList.get(currentQuestionIndex).getQtid());
        request.setAid(clickedAnswer != null ? Long.parseLong(clickedAnswer) : null);
        
        Log.d(TAG, "Sending answer: qtid=" + request.getQtid() + 
              ", aid=" + request.getAid() + 
              ", current question index=" + currentQuestionIndex);
              
        Gson gson = new Gson();
        String json = gson.toJson(request);
        
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.send("/app/submitAnswer", json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> Log.d(TAG, "Answer sent successfully"),
                    throwable -> Log.e(TAG, "Error sending answer", throwable)
                );
        } else {
            Log.e(TAG, "STOMP client not connected, cannot send answer");
            Toast.makeText(this, "Không thể gửi câu trả lời: Mất kết nối", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRanking(LobbyStatus status) {
        if (status == null || status.getParticipants() == null) {
            Log.e(TAG, "Received null status or participants list");
            return;
        }
        
        // Sort participants by score in descending order
        List<ParticipantStatus> sortedParticipants = new ArrayList<>(status.getParticipants());
        Collections.sort(sortedParticipants, (p1, p2) -> p2.getScore() - p1.getScore());
        
        int rank = 1;
        int yourScore = 0;
        String yourUsername = "";
        
        // Find your position in the sorted list
        for (int i = 0; i < sortedParticipants.size(); i++) {
            ParticipantStatus p = sortedParticipants.get(i);
            if (p.getUid().equals(uid)) {
                yourScore = p.getScore();
                yourUsername = p.getUsername();
                rank = i + 1; // Position is 1-based
                break;
            }
        }
        
        allScore = yourScore;
        
        // Display detailed ranking info
        binding.txtRankingPosition.setText(String.format("Rank: %d/%d", rank, sortedParticipants.size()));
        binding.txtCurrentScore.setText(String.format("Score: %d", allScore));
    }

    private void proceedToScoreActivity() {
        Intent scoreIntent = new Intent(MultiplayerQuestionActivity.this, ScoreActivity.class);
        scoreIntent.putExtra("score", allScore);
        scoreIntent.putExtra("qid", qid);
        scoreIntent.putExtra("lid", lid);
        scoreIntent.putExtra("isMultiPlayer", true);
        scoreIntent.putExtra("isHost", isHost);
        scoreIntent.putExtra("duration", quizDuration); // Pass the quiz duration
        scoreIntent.putParcelableArrayListExtra("questions", new ArrayList<>(questionList));
        scoreIntent.putStringArrayListExtra("selectedAnswers", new ArrayList<>(selectedAnswers));
        startActivity(scoreIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
