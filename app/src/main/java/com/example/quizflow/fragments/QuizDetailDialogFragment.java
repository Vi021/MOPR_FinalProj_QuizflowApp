package com.example.quizflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.models.UserModel;
import com.example.quizflow.utils.COLOR;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizDetailDialogFragment extends DialogFragment {
    private static QuizModel quiz;
    private static UserModel user;
    private static int position;

    public static QuizDetailDialogFragment newInstance(QuizModel quiz, UserModel user, int position) {
        QuizDetailDialogFragment fragment = new QuizDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("quiz", quiz);
        args.putSerializable("user", user);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_detailedquizitem, container, false);

        ConstraintLayout consL_topContainer = view.findViewById(R.id.consL_topContainer);
        CircleImageView cirImg_pfp = view.findViewById(R.id.cirImg_pfp);
        TextView txt_username = view.findViewById(R.id.txt_username);
        TextView txt_topic = view.findViewById(R.id.txt_topic);

        TextView txt_start = view.findViewById(R.id.txt_start);
        TextView txt_quizTitle = view.findViewById(R.id.txt_quizTitle);
        TextView txt_quizDesc = view.findViewById(R.id.txt_quizDesc);
        TextView txt_questionCount = view.findViewById(R.id.txt_questionCount);
        TextView txt_questionType = view.findViewById(R.id.txt_questionType);
        TextView txt_mcq = view.findViewById(R.id.txt_mcq);
        TextView txt_tf = view.findViewById(R.id.txt_tf);
        TextView txt_shortAns = view.findViewById(R.id.txt_shortAns);
        TextView txt_quizDuration = view.findViewById(R.id.txt_quizDuration);
        TextView txt_createdDate = view.findViewById(R.id.txt_createdDate);
        TextView txt_attemptCount = view.findViewById(R.id.txt_attemptCount);
        TextView txt_singlePlayer = view.findViewById(R.id.txt_singlePlayer);
        TextView txt_availability = view.findViewById(R.id.txt_availability);
        ImageView img_save = view.findViewById(R.id.img_save);
        ImageView img_share = view.findViewById(R.id.img_share);
        TextView img_report = view.findViewById(R.id.img_report);
        ImageView img_question = view.findViewById(R.id.img_question);
        ImageView img_mcq = view.findViewById(R.id.img_mcq);
        ImageView img_tf = view.findViewById(R.id.img_tf);
        ImageView img_shortAns = view.findViewById(R.id.img_shortAns);
        ImageView img_timer = view.findViewById(R.id.img_timer);
        ImageView img_clock = view.findViewById(R.id.img_clock);
        ImageView img_pen = view.findViewById(R.id.img_pen);
        ImageView img_singlePlayer = view.findViewById(R.id.img_singlePlayer);
        ImageView img_availability = view.findViewById(R.id.img_availability);

        Bundle args = getArguments();
        if (args != null) {
            quiz = (QuizModel) args.getSerializable("quiz");
            if (quiz != null) {
                // user part
                user = (UserModel) args.getSerializable("user");
                if (user != null) {
                    txt_username.setText(user.getUsername());
                    Glide.with(requireContext()).load(user.getPfp()).into(cirImg_pfp);
                } else {
                    txt_username.setText("Unknown");
                }
                txt_topic.setText(quiz.getTopic());

                txt_quizTitle.setText(quiz.getTitle());
                txt_quizDesc.setText(quiz.getDescription());
                txt_questionCount.setText(String.valueOf(quiz.getQuestionCount()));
                txt_quizDuration.setText(String.valueOf(quiz.getDuration()));
                txt_createdDate.setText(quiz.getCreatedDate());
                txt_attemptCount.setText(String.valueOf(quiz.getAttemptCount()));
                if (quiz.isPublic()) {
                    img_availability.setImageResource(R.drawable.ic_globe_white);
                } else {
                    img_availability.setImageResource(R.drawable.ic_lock_white);
                }
                txt_start.setOnClickListener(v -> {
                    // TODO: start quiz
                });
                img_save.setOnClickListener(v -> {
                    // TODO: add to collection
                });
                img_share.setOnClickListener(v -> {
                    // copy quiz id (code)
                });
                img_report.setOnClickListener(v -> {
                    // do nothing
                });


                // coloring
                position = args.getInt("position");
                Integer color1 = COLOR.QUIZ_ITEM.get(position % COLOR.count);
                Integer color2 = COLOR.QUIZ_ITEM.get(position % COLOR.count + COLOR.count);

                consL_topContainer.setBackgroundResource(color1);
                txt_quizTitle.setTextColor(color1);

                txt_start.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                txt_quizDesc.setTextColor(color2);
                txt_questionCount.setTextColor(color2);
                txt_questionType.setTextColor(color2);
                txt_mcq.setTextColor(color2);
                txt_tf.setTextColor(color2);
                txt_shortAns.setTextColor(color2);
                txt_quizDuration.setTextColor(color2);
                txt_createdDate.setTextColor(color2);
                txt_attemptCount.setTextColor(color2);
                txt_singlePlayer.setTextColor(color2);
                txt_availability.setTextColor(color2);
                img_save.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_share.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_report.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_question.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_mcq.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_tf.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_shortAns.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_timer.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_clock.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_pen.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_singlePlayer.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
                img_availability.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), color2));
            }
        }

        return view;
    }
}
