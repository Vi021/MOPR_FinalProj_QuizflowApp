package com.example.quizflow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quizflow.QuestionModel;
import com.example.quizflow.R;
import com.example.quizflow.activities.QuestionActivity;
import com.example.quizflow.activities.WaitingActivity;
import com.example.quizflow.requests.JoinLobbyRequest;
import com.example.quizflow.requests.LobbyRequest;
import com.example.quizflow.respones.LobbyResponse;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class QuizCodeDialogFragment extends DialogFragment {
    private static final String TAG = "QuizCodeDialogFragment";
    private boolean isSinglePlayer;

    public static QuizCodeDialogFragment newInstance(boolean isSinglePlayer) {
        QuizCodeDialogFragment fragment = new QuizCodeDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isSinglePlayer", isSinglePlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_quiz_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isSinglePlayer = getArguments().getBoolean("isSinglePlayer");
        }

        TextView txtPopupTitle = view.findViewById(R.id.txt_popupTitle);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        TextInputEditText editQuizCode = view.findViewById(R.id.edit_quizCode);

        txtPopupTitle.setText(isSinglePlayer ? "Enter Quiz ID" : "Enter Quiz ID or Lobby Code");
        editQuizCode.setHint(isSinglePlayer ? "Quiz ID" : "Quiz ID or Lobby Code");

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            String inputCode = editQuizCode.getText().toString().trim();
            if (inputCode.isEmpty()) {
                editQuizCode.setError("Please enter a Quiz ID or Lobby Code");
                return;
            }

            Long uid = Utilities.getUID(requireContext());
            if (uid == null && !isSinglePlayer) {
                Toast.makeText(requireContext(), "Please sign in to play multiplayer", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }

            if (isSinglePlayer) {
                // Single player always uses Quiz ID
                if (Utilities.isNotValidQuizId(inputCode)) {
                    editQuizCode.setError("Enter a valid Quiz ID");
                    return;
                }
                long qid = Long.parseLong(inputCode);
                loadQuizAndStartGame(qid);
            } else {
                // For multiplayer, first try to interpret as a lobby code
                checkAndJoinLobby(inputCode, uid);
            }
        });
    }
    
    private void loadQuizAndStartGame(long qid) {
        Utilities.getQuizByIdAsync(requireContext(), qid, new Utilities.QuizCallback() {
            @Override
            public void onSuccess(QuizResponse quiz) {
                Intent intent = new Intent(requireContext(), QuestionActivity.class);
                intent.putExtra("qid", qid);
                intent.putExtra("isMultiPlayer", false);
                startActivity(intent);
                dismiss();
            }

            @Override
            public void onFailure(String error) {
                showError("Invalid Quiz ID: " + error);
            }
        });
    }
    
    private void loadQuizAndCreateLobby(long qid, Long uid) {
        Utilities.getQuizByIdAsync(requireContext(), qid, new Utilities.QuizCallback() {
            @Override
            public void onSuccess(QuizResponse quiz) {
                // Create new lobby with this quiz
                LobbyRequest request = new LobbyRequest();
                request.setQid(qid);
                request.setUid(uid);
                Utilities.createLobbyAsync(requireContext(), request, new Utilities.LobbyCallback() {
                    @Override
                    public void onSuccess(LobbyResponse lobby) {
                        Intent intent = new Intent(requireContext(), WaitingActivity.class);
                        intent.putExtra("lid", lobby.getLid());
                        intent.putExtra("uid", uid);
                        intent.putExtra("code", lobby.getCode());
                        intent.putExtra("isHost", true);
                        startActivity(intent);
                        dismiss();
                    }

                    @Override
                    public void onFailure(String error) {
                        showError("Failed to create lobby: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                showError("Invalid Quiz ID: " + error);
            }
        });
    }
    
    private void joinLobby(String code, Long uid) {
        Log.d(TAG, "Checking if lobby code exists: " + code);
        
        // First, check if the lobby code exists
        Utilities.checkLobbyCodeAsync(requireContext(), code, new Utilities.LobbyCodeCallback() {
            @Override
            public void onSuccess(boolean exists) {
                if (exists) {
                    Log.d(TAG, "Lobby code exists, joining lobby: " + code);
                    // Lobby code exists, proceed with joining
                    JoinLobbyRequest request = new JoinLobbyRequest();
                    request.setCode(code);
                    request.setUid(uid);
                    
                    Utilities.joinLobbyAsync(requireContext(), request, new Utilities.LobbyCallback() {
                        @Override
                        public void onSuccess(LobbyResponse lobby) {
                            Log.d(TAG, "Successfully joined lobby: lid=" + lobby.getLid() + ", code=" + lobby.getCode() + ", qid=" + lobby.getQid());
                            Intent intent = new Intent(requireContext(), WaitingActivity.class);
                            intent.putExtra("lid", lobby.getLid());
                            intent.putExtra("uid", uid);
                            intent.putExtra("code", lobby.getCode());
                            intent.putExtra("isHost", false);
                            startActivity(intent);
                            dismiss();
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e(TAG, "Failed to join lobby with code: " + code + ", error: " + error);
                            showError("Error joining lobby: " + error);
                        }
                    });
                } else {
                    Log.e(TAG, "Lobby code does not exist: " + code);
                    showError("Invalid lobby code. Please check and try again.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to check if lobby code exists: " + code + ", error: " + error);
                showError("Error checking lobby code: " + error);
            }
        });
    }
    
    private void checkAndJoinLobby(String inputCode, Long uid) {
        Log.d(TAG, "Checking if code is a valid lobby code: " + inputCode);
        
        // First, check if it's a valid lobby code
        Utilities.checkLobbyCodeAsync(requireContext(), inputCode, new Utilities.LobbyCodeCallback() {
            @Override
            public void onSuccess(boolean exists) {
                if (exists) {
                    Log.d(TAG, "Code is a valid lobby code, joining: " + inputCode);
                    // It's a valid lobby code, join it
                    joinLobby(inputCode, uid);
                } else {
                    Log.d(TAG, "Code is not a valid lobby code, trying as a quiz ID: " + inputCode);
                    // Not a valid lobby code, try as a quiz ID
                    tryAsQuizId(inputCode, uid);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Error checking lobby code: " + error);
                // Network error, try as quiz ID as fallback
                tryAsQuizId(inputCode, uid);
            }
        });
    }
    
    private void tryAsQuizId(String inputCode, Long uid) {
        // Check if it looks like a valid quiz ID (numeric and 6+ digits)
        if (inputCode.matches("\\d+") && inputCode.length() >= 6) {
            try {
                long qid = Long.parseLong(inputCode);
                Log.d(TAG, "Trying code as quiz ID: " + qid);
                loadQuizAndCreateLobby(qid, uid);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing code as quiz ID: " + e.getMessage());
                showError("Invalid input format. Please enter a valid Lobby Code or Quiz ID.");
            }
        } else {
            Log.e(TAG, "Input is neither a valid lobby code nor a valid quiz ID format: " + inputCode);
            showError("Invalid input. Please enter a valid Lobby Code or Quiz ID.");
        }
    }
    
    private void showError(String message) {
        TextInputEditText editQuizCode = getView().findViewById(R.id.edit_quizCode);
        if (editQuizCode != null) {
            editQuizCode.setError(message);
        }
        Utilities.showError(requireContext(), TAG, message);
    }
}
