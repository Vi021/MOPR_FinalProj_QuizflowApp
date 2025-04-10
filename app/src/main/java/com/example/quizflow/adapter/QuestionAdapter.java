package com.example.quizflow.adapter;

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

    private final String correctAnswer;
    private final List<String> users;
    private final Score returnScore;
    private ViewholderQuestionBinding binding;

    public interface Score {
        void amount(int number, String clickedAnswer);
    }

    public QuestionAdapter(String correctAnswer, List<String> users, Score returnScore) {
        this.correctAnswer = correctAnswer;
        this.users = users != null ? new ArrayList<>(users) : new ArrayList<>();
        this.returnScore = returnScore;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ViewholderQuestionBinding.inflate(inflater, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ViewholderQuestionBinding binding = holder.binding;
        binding.answerTxt.setText(differ.getCurrentList().get(position));

        final int currentPos = getCurrentPosition(correctAnswer);

        if (differ.getCurrentList().size() == 5 && currentPos == position) {
            binding.answerTxt.setBackgroundResource(R.drawable.green_bg);
            binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.tick),
                    null
            );
        }

        if (differ.getCurrentList().size() == 5) {
            int clickedPos = getCurrentPosition(differ.getCurrentList().get(4));

            if (clickedPos == position && clickedPos != currentPos) {
                binding.answerTxt.setBackgroundResource(R.drawable.red_bg);
                binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.thieves),
                        null
                );
            }
        }

        if (position == 4) {
            binding.getRoot().setVisibility(View.GONE);
        }

        final int finalPosition = position;
        holder.itemView.setOnClickListener(v -> {
            String str = getAnswerString(finalPosition);

            if (users.size() > 4) {
                users.set(4, str);
            } else {
                users.add(str);
            }
            notifyDataSetChanged();

            if (currentPos == finalPosition) {
                binding.answerTxt.setBackgroundResource(R.drawable.green_bg);
                binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.tick),
                        null
                );
                returnScore.amount(5, str);
            } else {
                binding.answerTxt.setBackgroundResource(R.drawable.red_bg);
                binding.answerTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.thieves),
                        null
                );
                returnScore.amount(0, str);
            }
        });

        if (differ.getCurrentList().size() == 5) {
            holder.itemView.setOnClickListener(null);
        }
    }

    private int getCurrentPosition(String answer) {
        switch (answer) {
            case "a": return 0;
            case "b": return 1;
            case "c": return 2;
            case "d": return 3;
            default: return 0;
        }
    }

    private String getAnswerString(int position) {
        switch (position) {
            case 0: return "a";
            case 1: return "b";
            case 2: return "c";
            case 3: return "d";
            default: return "";
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    private final DiffUtil.ItemCallback<String> differCallback = new DiffUtil.ItemCallback<String>() {
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