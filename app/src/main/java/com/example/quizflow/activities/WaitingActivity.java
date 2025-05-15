package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.QuestionModel;
import com.example.quizflow.R;
import com.example.quizflow.adapters.ParticipantAdapter;
import com.example.quizflow.models.LobbyStatus;
import com.example.quizflow.models.ParticipantStatus;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WaitingActivity extends AppCompatActivity {
    private static final String TAG = "WaitingActivity";
    private TextView txtQuizCode;
    private RecyclerView participantList;
    private Button btnStart, btnCancel;
    private ParticipantAdapter participantAdapter;
    private List<ParticipantStatus> participants = new ArrayList<>();
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private Long uid, lid;
    private String code;
    private boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        txtQuizCode = findViewById(R.id.txt_quizCode);
        participantList = findViewById(R.id.participantList);
        btnStart = findViewById(R.id.btn_start);
        btnCancel = findViewById(R.id.btn_cancel);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        uid = intent.getLongExtra("uid", -1);
        lid = intent.getLongExtra("lid", -1);
        code = intent.getStringExtra("code");
        isHost = intent.getBooleanExtra("isHost", false);

        if (uid == -1 || (lid == -1 && code == null)) {
            Toast.makeText(this, "Invalid lobby data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtQuizCode.setText("Quiz Code: " + (code != null ? code : "N/A"));

        // Thiết lập RecyclerView
        participantAdapter = new ParticipantAdapter(participants);
        participantList.setLayoutManager(new LinearLayoutManager(this));
        participantList.setAdapter(participantAdapter);

        // Hiển thị nút Start cho host
        btnStart.setVisibility(isHost ? View.VISIBLE : View.GONE);

        // Kết nối WebSocket
        connectWebSocket();

        // Xử lý nút Start (chỉ host)
        btnStart.setOnClickListener(v -> {
            Log.d(TAG, "Start button clicked. Starting lobby with lid=" + lid + ", uid=" + uid + ", participants=" + participants.size());
            Toast.makeText(WaitingActivity.this, "Starting game...", Toast.LENGTH_SHORT).show();
            
            Utilities.startLobbyAsync(this, lid, uid, new Utilities.GenericCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "startLobbyAsync success callback called");
                    Toast.makeText(WaitingActivity.this, "Game started!", Toast.LENGTH_SHORT).show();
                    
                    // Trực tiếp chuyển sang màn hình game sau khi bắt đầu thành công
                    // Không đợi thông báo từ WebSocket
                    Log.d(TAG, "Transitioning to game directly (not waiting for WebSocket)");
                    Intent intent = new Intent(WaitingActivity.this, MultiplayerQuestionActivity.class);
                    intent.putExtra("lid", lid);
                    intent.putExtra("uid", uid);
                    
                    // Lấy qid từ lobby hiện tại nếu có
                    Utilities.getLobbyInfoAsync(WaitingActivity.this, lid, new Utilities.LobbyInfoCallback() {
                        @Override
                        public void onSuccess(Long qid) {
                            intent.putExtra("qid", qid);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            // Nếu không lấy được thông tin lobby, sử dụng phương pháp thay thế
                            Log.e(TAG, "Error getting lobby info: " + error);
                            Toast.makeText(WaitingActivity.this, "Error: " + error + ". Try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "startLobbyAsync failure callback: " + error);
                    Toast.makeText(WaitingActivity.this, "Failed to start game: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý nút Cancel
        btnCancel.setOnClickListener(v -> {
            if (compositeDisposable != null) {
                compositeDisposable.dispose();
            }
            if (stompClient != null) {
                stompClient.disconnect();
            }
            finish();
        });
    }

    private void connectWebSocket() {
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
                            Toast.makeText(WaitingActivity.this, 
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
                        if (status.getLid().equals(lid)) {
                            Log.d(TAG, "Updating participants list: " + status.getParticipants().size() + " participants");
                            participants.clear();
                            participants.addAll(status.getParticipants());
                            participantAdapter.notifyDataSetChanged();
                            if (status.getStatus() != null && status.getStatus().equals("STARTED")) {
                                Log.d(TAG, "Lobby status is STARTED, transitioning to game");
                                Intent intent = new Intent(WaitingActivity.this, MultiplayerQuestionActivity.class);
                                intent.putExtra("lid", lid);
                                intent.putExtra("uid", uid);
                                intent.putExtra("qid", status.getQid());
                                startActivity(intent);
                                finish();
                            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}
