package com.example.quizflow.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.activities.AccountActivity;
import com.example.quizflow.activities.SearchActivity;
import com.example.quizflow.activities.SigninActivity;
import com.example.quizflow.activities.QuizEditorActivity;
import com.example.quizflow.activities.QuestionActivity;
import com.example.quizflow.activities.WaitingActivity;
import com.example.quizflow.adapters.TopicAdapter;
import com.example.quizflow.QuestionModel;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.respones.QuizResponse;
import com.example.quizflow.respones.UserResponse;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private ConstraintLayout consL_home, consL_accountBar, consL_profileBar, consL_earncoinsBar;
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
        consL_earncoinsBar = view.findViewById(R.id.consL_earncoinsBar);

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
            startActivity(new Intent(requireContext(), QuizEditorActivity.class));
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

        TextView txt_startNow = view.findViewById(R.id.txt_startNow);
        txt_startNow.setOnClickListener(this::noService);
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

        txtPopupTitle.setText(isSinglePlayer ? "Enter Quiz ID" : "Enter Multiplayer Quiz ID");

        // dialog enter animation
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_enter);
        dialogView.startAnimation(animation);

        // cancel action
        btnCancel.setOnClickListener(v -> {
            btnCancel.setAlpha(0.5f);
            new Handler().postDelayed(() -> btnCancel.setAlpha(1.0f), 200);
            dialog.dismiss();
        });

        // confirm action
        btnConfirm.setOnClickListener(v -> {
            String quizCode = editQuizCode.getText().toString().trim();
            if (Utilities.isNotValidQuizId(quizCode)) {
                editQuizCode.setError("Enter a valid quiz ID");
                return;
            }

            long qid = Long.parseLong(quizCode);
            if (isSinglePlayer) {
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
                        editQuizCode.setError("Invalid quiz ID");
                        Utilities.showError(context, "HomeFragment", error);
                    }
                });
            } else {
                // Placeholder for multiplayer
                Toast.makeText(context, "Multiplayer mode not available", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void validate() {
        if (!signedIn) {
            consL_accountBar.setVisibility(View.VISIBLE);
            consL_profileBar.setVisibility(View.GONE);
            lineL_actionBar.setVisibility(View.GONE);
            consL_earncoinsBar.setVisibility(View.GONE);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(consL_home);
            constraintSet.connect(R.id.consL_historyBar, ConstraintSet.TOP, R.id.lineL_srchBar, ConstraintSet.BOTTOM);
            constraintSet.applyTo(consL_home);
        } else {
            consL_accountBar.setVisibility(View.GONE);
            consL_profileBar.setVisibility(View.VISIBLE);
            lineL_actionBar.setVisibility(View.VISIBLE);
            consL_earncoinsBar.setVisibility(View.VISIBLE);
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

//    // Question list data
//    private List<QuestionModel> questionList() {
//        List<QuestionModel> questions = new ArrayList<>();
//
//        questions.add(new QuestionModel(
//                1,
//                "Which planet is the largest planet in the solar system?",
//                "Earth",
//                "Mars",
//                "Neptune",
//                "Jupiter",
//                "d",
//                5,
//                "q_1",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                2,
//                "Which country is the largest country in the world by land area?",
//                "Russia",
//                "Canada",
//                "United States",
//                "China",
//                "a",
//                5,
//                "q_2",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                3,
//                "Which of the following substances is used as an anti-cancer medication in the medical world?",
//                "Cheese",
//                "Lemon juice",
//                "Cannabis",
//                "Paspalum",
//                "c",
//                5,
//                "q_3",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                4,
//                "Which moon in the Earth's solar system has an atmosphere?",
//                "Luna (Moon)",
//                "Phobos (Mars' moon)",
//                "Venus' moon",
//                "None of the above",
//                "d",
//                5,
//                "q_4",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                5,
//                "Which of the following symbols represents the chemical element with the atomic number 6?",
//                "O",
//                "H",
//                "C",
//                "N",
//                "c",
//                5,
//                "q_5",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                6,
//                "Who is credited with inventing theater as we know it today?",
//                "Shakespeare",
//                "Arthur Miller",
//                "Ashkouri",
//                "Ancient Greeks",
//                "d",
//                5,
//                "q_6",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                7,
//                "Which ocean is the largest ocean in the world?",
//                "Pacific Ocean",
//                "Atlantic Ocean",
//                "Indian Ocean",
//                "Arctic Ocean",
//                "a",
//                5,
//                "q_7",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                8,
//                "Which religions are among the most practiced religions in the world?",
//                "Islam, Christianity, Judaism",
//                "Buddhism, Hinduism, Sikhism",
//                "Zoroastrianism, Brahmanism, Yazdanism",
//                "Taoism, Shintoism, Confucianism",
//                "a",
//                5,
//                "q_8",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                9,
//                "In which continent are the most independent countries located?",
//                "Asia",
//                "Europe",
//                "Africa",
//                "Americas",
//                "c",
//                5,
//                "q_9",
//                null
//        ));
//
//        questions.add(new QuestionModel(
//                10,
//                "Which ocean has the greatest average depth?",
//                "Pacific Ocean",
//                "Atlantic Ocean",
//                "Indian Ocean",
//                "Southern Ocean",
//                "d",
//                5,
//                "q_10",
//                null
//        ));
//
//        return questions;
//    }
}