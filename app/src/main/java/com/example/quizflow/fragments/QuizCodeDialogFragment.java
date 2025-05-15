package com.example.quizflow.fragments;

import android.content.Intent;
import android.os.Bundle;
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
                if (Utilities.isNotValidQuizId(inputCode)) {
                    editQuizCode.setError("Enter a valid Quiz ID");
                    return;
                }
                long qid = Long.parseLong(inputCode);
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
                        editQuizCode.setError("Invalid Quiz ID");
                        Utilities.showError(requireContext(), "QuizCodeDialog", error);
                    }
                });
            } else {
                // Multiplayer: Kiểm tra xem input là Quiz ID (số) hay Lobby Code (chuỗi)
                if (inputCode.matches("\\d+")) { // If numeric (Quiz ID)
                    if (Utilities.isNotValidQuizId(inputCode)) {
                        editQuizCode.setError("Enter a valid Quiz ID");
                        return;
                    }
                    long qid = Long.parseLong(inputCode);
                    Utilities.getQuizByIdAsync(requireContext(), qid, new Utilities.QuizCallback() {
                        @Override
                        public void onSuccess(QuizResponse quiz) {
                            // Create new lobby
                            LobbyRequest request = new LobbyRequest();
                            request.setQid(qid);
                            request.setUid(uid);
                            Utilities.createLobbyAsync(requireContext(), request, new Utilities.LobbyCallback() {
                                @Override
                                public void onSuccess(LobbyResponse lobby) { // Sửa Utilities.LobbyResponse thành LobbyResponse
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
                                    editQuizCode.setError("Failed to create lobby");
                                    Utilities.showError(requireContext(), "QuizCodeDialog", error);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            editQuizCode.setError("Invalid Quiz ID");
                            Utilities.showError(requireContext(), "QuizCodeDialog", error);
                        }
                    });
                } else { // If string (Lobby Code)
                    JoinLobbyRequest request = new JoinLobbyRequest();
                    request.setCode(inputCode);
                    request.setUid(uid);
                    Utilities.joinLobbyAsync(requireContext(), request, new Utilities.LobbyCallback() {
                        @Override
                        public void onSuccess(LobbyResponse lobby) { // Sửa Utilities.LobbyResponse thành LobbyResponse
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
                            editQuizCode.setError("Invalid Lobby Code");
                            Utilities.showError(requireContext(), "QuizCodeDialog", error);
                        }
                    });
                }
            }
        });
    }
}
