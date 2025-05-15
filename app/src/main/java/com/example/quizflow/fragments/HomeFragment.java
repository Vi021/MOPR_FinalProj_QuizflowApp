package com.example.quizflow.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.activities.AccountActivity;
import com.example.quizflow.activities.SearchActivity;
import com.example.quizflow.activities.SigninActivity;
import com.example.quizflow.activities.QuizEditor2Activity;
import com.example.quizflow.activities.QuestionActivity;
import com.example.quizflow.activities.WaitingActivity;
import com.example.quizflow.adapters.TopicAdapter;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.requests.JoinLobbyRequest;
import com.example.quizflow.requests.LobbyRequest;
import com.example.quizflow.respones.LobbyResponse;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private ConstraintLayout consL_home, consL_accountBar, consL_profileBar;
    private LinearLayout lineL_actionBar;
    private TextView txt_hello, txt_coins;
    private CircleImageView cirImg_pfp;
    private boolean signedIn = false;
    private String fullname, username, image, email;
    private int coins;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initFindView(view);
        validate();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // update data each 2s
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            validate();
            loadUser(Utilities.getUID(requireContext()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remove live update
    }

    private void initFindView(View view) {
        txt_hello = view.findViewById(R.id.txt_hello);
        txt_coins = view.findViewById(R.id.txt_coins);
        cirImg_pfp = view.findViewById(R.id.cirImg_pfp);
        consL_home = view.findViewById(R.id.homeContainer);
        consL_accountBar = view.findViewById(R.id.consL_accountBar);
        consL_profileBar = view.findViewById(R.id.consL_profileBar);
        lineL_actionBar = view.findViewById(R.id.lineL_actionBar);

        /// Check if user is signed in by UID
        Long uid = Utilities.getUID(requireContext());
        signedIn = (uid != null);

        loadUser(uid);

        TextView txt_btnSignInSignUp = view.findViewById(R.id.txt_btnSignInSignUp);
        txt_btnSignInSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SigninActivity.class);
            startActivity(intent);
            //requireActivity().finish();
        });

        cirImg_pfp.setOnClickListener(view1 -> {
            Intent intentProfile = new Intent(requireActivity(), AccountActivity.class);
            startActivity(intentProfile);
        });

        LinearLayout lineL_srchBar = view.findViewById(R.id.lineL_srchBar);
        lineL_srchBar.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            startActivity(intent);
        });

        LinearLayout lineL_createQuiz = view.findViewById(R.id.lineL_createQuiz);
        lineL_createQuiz.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), QuizEditor2Activity.class));
        });

        LinearLayout lineL_singlePlayer = view.findViewById(R.id.lineL_singlePlayer);
        lineL_singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuizCodeDialog(requireContext(), true);
            }
        });

        LinearLayout lineL_multiPlayers = view.findViewById(R.id.lineL_multiPlayers);
        lineL_multiPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuizCodeDialog(requireContext(), false);
            }
        });

        TextView txt_viewCategories = view.findViewById(R.id.txt_viewHistory);
        txt_viewCategories.setOnClickListener(this::noCategories);

        RecyclerView recy_categories = view.findViewById(R.id.recy_categories);
        recy_categories.setAdapter(new TopicAdapter(requireContext(), TYPE.TOPICS));
        recy_categories.setHasFixedSize(true);
        recy_categories.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));
    }

    private void loadUser(Long uid) {
        if (signedIn) {
            // Fetch user data from API
            Utilities.getUserByUidAsync(requireContext(), uid, new Utilities.AccountCallback() {
                @Override
                public void onSuccess(AccountModel user) {
                    signedIn = true;
                    fullname = user.getFullname();
                    username = user.getUsername();
                    image = user.getImage();
                    email = user.getEmail();
                    coins = user.getCoins();
                    updateUserInfo();

                    if (image != null && !image.isEmpty()) {
                        String imageUrl = Refs.BASE_IMAGE_URL + image;
                        Glide.with(HomeFragment.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_pfp_icebear)
                                .error(R.drawable.ic_default_pfp_icebear)
                                .into(cirImg_pfp);
                    } else {
                        cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
                    }
                }

                @Override
                public void onFailure(String error) {
                    signedIn = false;
                    cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
                    updateUserInfo();
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
            updateUserInfo();
        }
    }

    private void updateUserInfo() {
        if (signedIn) {
            if (fullname != null && !fullname.isEmpty()) {
                txt_hello.setText("Hello, " + fullname + "!");
            } else if (username != null && !username.isEmpty()) {
                txt_hello.setText("Hello, " + username + "!");
            } else {
                txt_hello.setText("Hello!");
            }
            if (txt_coins != null) {
                txt_coins.setText(String.valueOf(coins));
            }
        } else {
            txt_hello.setText("Hello!");
            if (txt_coins != null) {
                txt_coins.setText("0");
            }
        }
    }

    private void showQuizCodeDialog(Context context, boolean isSinglePlayer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_quiz_code, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false); // no exit on back press or outside touch
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView txtPopupTitle = dialogView.findViewById(R.id.txt_popupTitle);
        TextInputEditText editQuizCode = dialogView.findViewById(R.id.edit_quizCode);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        txtPopupTitle.setText(isSinglePlayer ? "Enter Quiz ID" : "Enter Quiz ID or Lobby Code");
        editQuizCode.setHint(isSinglePlayer ? "Quiz ID" : "Quiz ID or Lobby Code");

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_enter);
        dialogView.startAnimation(animation);

        btnCancel.setOnClickListener(v -> {
            btnCancel.setAlpha(0.5f);
            new Handler().postDelayed(() -> btnCancel.setAlpha(1.0f), 200);
            dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            String inputCode = editQuizCode.getText().toString().trim();
            if (inputCode.isEmpty()) {
                editQuizCode.setError("Please enter a Quiz ID or Lobby Code");
                return;
            }

            Long uid = Utilities.getUID(context);
            if (uid == null && !isSinglePlayer) {
                Toast.makeText(context, "Please sign in to play multiplayer", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            if (isSinglePlayer) {
                // Single player always uses Quiz ID
                if (Utilities.isNotValidQuizId(inputCode)) {
                    editQuizCode.setError("Enter a valid Quiz ID");
                    return;
                }
                long qid = Long.parseLong(inputCode);
                // Start single player game
                Utilities.getQuizByIdAsync(context, qid, new Utilities.QuizCallback() {
                    @Override
                    public void onSuccess(QuizResponse quiz) {
                        Intent intent = new Intent(context, QuestionActivity.class);
                        intent.putExtra("qid", qid);
                        intent.putExtra("isMultiPlayer", false);
                        context.startActivity(intent);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(String error) {
                        editQuizCode.setError("Invalid Quiz ID");
                        Utilities.showError(context, "HomeFragment", error);
                    }
                });
            } else {
                // For multiplayer, first check if it's a valid lobby code
                checkAndJoinLobby(context, inputCode, uid, editQuizCode, dialog);
            }
        });

        dialog.show();
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    
    private void checkAndJoinLobby(Context context, String inputCode, Long uid, TextInputEditText editQuizCode, AlertDialog dialog) {
        Log.d("HomeFragment", "Checking if code is a valid lobby code: " + inputCode);
        
        // First try as a lobby code
        Utilities.checkLobbyCodeAsync(context, inputCode, new Utilities.LobbyCodeCallback() {
            @Override
            public void onSuccess(boolean exists) {
                if (exists) {
                    Log.d("HomeFragment", "Code is a valid lobby code, joining: " + inputCode);
                    // It's a valid lobby code, join it
                    joinLobby(context, inputCode, uid, editQuizCode, dialog);
                } else {
                    Log.d("HomeFragment", "Code is not a valid lobby code, trying as a quiz ID: " + inputCode);
                    // Not a valid lobby code, try as a quiz ID
                    tryAsQuizId(context, inputCode, uid, editQuizCode, dialog);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error checking lobby code: " + error);
                // Network error, try as quiz ID as fallback
                tryAsQuizId(context, inputCode, uid, editQuizCode, dialog);
            }
        });
    }
    
    private void joinLobby(Context context, String code, Long uid, TextInputEditText editQuizCode, AlertDialog dialog) {
        JoinLobbyRequest request = new JoinLobbyRequest();
        request.setCode(code);
        request.setUid(uid);
        
        Utilities.joinLobbyAsync(context, request, new Utilities.LobbyCallback() {
            @Override
            public void onSuccess(LobbyResponse lobby) {
                Log.d("HomeFragment", "Successfully joined lobby: lid=" + lobby.getLid() + ", code=" + lobby.getCode() + ", qid=" + lobby.getQid());
                Intent intent = new Intent(context, WaitingActivity.class);
                intent.putExtra("lid", lobby.getLid());
                intent.putExtra("uid", uid);
                intent.putExtra("code", lobby.getCode());
                intent.putExtra("isHost", false);
                context.startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Failed to join lobby with code: " + code + ", error: " + error);
                editQuizCode.setError("Error joining lobby");
                Utilities.showError(context, "HomeFragment", error);
            }
        });
    }
    
    private void tryAsQuizId(Context context, String inputCode, Long uid, TextInputEditText editQuizCode, AlertDialog dialog) {
        // Check if it looks like a valid quiz ID (numeric and 6+ digits)
        if (inputCode.matches("\\d+") && inputCode.length() >= 6) {
            try {
                long qid = Long.parseLong(inputCode);
                Log.d("HomeFragment", "Trying code as quiz ID: " + qid);
                // Try to create a lobby with this quiz ID
                Utilities.getQuizByIdAsync(context, qid, new Utilities.QuizCallback() {
                    @Override
                    public void onSuccess(QuizResponse quiz) {
                        LobbyRequest request = new LobbyRequest();
                        request.setQid(qid);
                        request.setUid(uid);
                        Utilities.createLobbyAsync(context, request, new Utilities.LobbyCallback() {
                            @Override
                            public void onSuccess(LobbyResponse lobby) {
                                Intent intent = new Intent(context, WaitingActivity.class);
                                intent.putExtra("lid", lobby.getLid());
                                intent.putExtra("uid", uid);
                                intent.putExtra("code", lobby.getCode());
                                intent.putExtra("isHost", true);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(String error) {
                                editQuizCode.setError("Failed to create lobby");
                                Utilities.showError(context, "HomeFragment", error);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        editQuizCode.setError("Invalid Quiz ID");
                        Utilities.showError(context, "HomeFragment", error);
                    }
                });
            } catch (NumberFormatException e) {
                Log.e("HomeFragment", "Error parsing code as quiz ID: " + e.getMessage());
                editQuizCode.setError("Invalid input format. Please enter a valid Lobby Code or Quiz ID.");
                Utilities.showError(context, "HomeFragment", "Invalid number format: " + e.getMessage());
            }
        } else {
            Log.e("HomeFragment", "Input is neither a valid lobby code nor a valid quiz ID format: " + inputCode);
            editQuizCode.setError("Invalid input. Please enter a valid Lobby Code or Quiz ID.");
            Utilities.showError(context, "HomeFragment", "Invalid input format");
        }
    }

    private void validate() {
        if (!signedIn) {
            consL_accountBar.setVisibility(View.VISIBLE);
            consL_profileBar.setVisibility(View.GONE);
            lineL_actionBar.setVisibility(View.GONE);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(consL_home);
            constraintSet.connect(R.id.consL_historyBar, ConstraintSet.TOP, R.id.lineL_srchBar, ConstraintSet.BOTTOM);
            constraintSet.applyTo(consL_home);
        } else {
            consL_accountBar.setVisibility(View.GONE);
            consL_profileBar.setVisibility(View.VISIBLE);
            lineL_actionBar.setVisibility(View.VISIBLE);
        }
        updateUserInfo();
    }

    // defaults
    public void noCategories(View view) {
        Toast toast = Toast.makeText(requireContext(), "No more categories available!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }

    public void noService(View view) {
        Toast toast = Toast.makeText(requireContext(), "Service unavailable!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }

    public void noQuizzes(View view) {
        Toast toast = Toast.makeText(requireContext(), "No quizzes found!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }
}