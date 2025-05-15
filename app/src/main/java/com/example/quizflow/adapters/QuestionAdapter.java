package com.example.quizflow.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.databinding.ViewholderQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Viewholder> {
    private static final String TAG = "QuestionAdapter";
    private final String correctAnswer;
    private final List<String> answers; // Thêm biến answers
    private final List<String> aids;
    private final Score returnScore;
    private String clickedAnswer = null;

    public interface Score {
        void amount(int number, String clickedAnswer);
    }

    public QuestionAdapter(String correctAnswer, List<String> answers, List<String> aids, Score returnScore) {
        this.correctAnswer = correctAnswer;
        this.answers = answers != null ? new ArrayList<>(answers) : new ArrayList<>();
        this.aids = aids != null ? new ArrayList<>(aids) : new ArrayList<>();
        this.returnScore = returnScore;
        Log.d(TAG, "Initialized: correctAnswer=" + correctAnswer + ", answers=" + answers + ", aids=" + aids);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderQuestionBinding binding = ViewholderQuestionBinding.inflate(inflater, parent, false);
        return new Viewholder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ViewholderQuestionBinding binding = holder.binding;
        String answer = differ.getCurrentList().get(position);
        String aid = aids.get(position);
        binding.answerTxt.setText(answer);
        Log.d(TAG, "Binding position=" + position + ", answer=" + answer + ", aid=" + aid);

        // Reset giao diện
        binding.answerTxt.setBackgroundResource(R.drawable.answer_white);
        binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);

        // Nếu đã có lựa chọn (clickedAnswer), cập nhật giao diện
        if (clickedAnswer != null) {
            if (aid.equals(correctAnswer)) {
                // Đáp án đúng
                binding.answerTxt.setBackgroundResource(R.drawable.green_bg);
                binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.tick),
                        null
                );
            } else if (aid.equals(clickedAnswer)) {
                // Đáp án sai đã chọn
                binding.answerTxt.setBackgroundResource(R.drawable.red_bg);
                binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.thieves),
                        null
                );
            }
            // Vô hiệu hóa click sau khi đã chọn
            holder.itemView.setOnClickListener(null);
        } else {
            // Xử lý click để chọn đáp án
            holder.itemView.setOnClickListener(v -> {
                Log.d(TAG, "Clicked position=" + position + ", answer=" + answer + ", aid=" + aid);
                clickedAnswer = aid;
                int score = aid.equals(correctAnswer) ? 5 : 0;
                returnScore.amount(score, aid);
                Log.d(TAG, "Score for aid=" + aid + ": " + score + ", correctAnswer=" + correctAnswer);
                notifyDataSetChanged(); // Cập nhật giao diện
            });
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    private final DiffUtil.ItemCallback<String> differCallback = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public final AsyncListDiffer<String> differ = new AsyncListDiffer<>(this, differCallback);

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderQuestionBinding binding;

        public Viewholder(ViewholderQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}