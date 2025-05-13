package com.example.quizflow.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.models.AnswerModel;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
    private Context context;

    private List<AnswerModel> answers;
    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }
    public List<AnswerModel> getAnswers() {
        return answers;
    }

    private Runnable onAnswerChanged;
    private long qid;

    private static int MIN_ANSWER = 2;

    public AnswerAdapter(Context context, List<AnswerModel> answers) {
        this.context = context;
        this.answers = answers;
    }

    public AnswerAdapter(Context context, List<AnswerModel> answers, Runnable onAnswerChanged) {
        this.context = context;
        this.answers = answers;
        this.onAnswerChanged = onAnswerChanged;
    }

    public AnswerAdapter(Context context, List<AnswerModel> answers, long qid) {
        this.context = context;
        this.answers = answers;
        this.qid = qid;
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_answerLetter;
        private EditText eTxt_answer;
        private RadioButton radBtn_correctAns;
        private ImageView img_remove;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_answerLetter = itemView.findViewById(R.id.txt_answerLetter);
            eTxt_answer = itemView.findViewById(R.id.eTxt_answer);
            radBtn_correctAns = itemView.findViewById(R.id.radBtn_correctAns);
            img_remove = itemView.findViewById(R.id.img_remove);
        }
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.row_answeritem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        AnswerModel answer = answers.get(position);

        holder.txt_answerLetter.setText(String.valueOf((char) ('A' + position)));

        holder.eTxt_answer.setText(answer.getText());
        if (holder.eTxt_answer.getTag() instanceof TextWatcher) {
            holder.eTxt_answer.removeTextChangedListener((TextWatcher) holder.eTxt_answer.getTag());
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO: no empty answer
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                answer.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO: no empty answer
            }
        };
        holder.eTxt_answer.addTextChangedListener(watcher);

        holder.radBtn_correctAns.setChecked(answer.isCorrect());
        holder.radBtn_correctAns.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < answers.size(); i++) {
                    answers.get(i).setCorrect(i == position);
                }

                notifyDataSetChanged();
            }
        });

        holder.img_remove.setEnabled(answers.size() > MIN_ANSWER);
        holder.img_remove.setOnClickListener(v -> removeAnswer(position));
    }

    @Override
    public int getItemCount() {
        return answers != null ? answers.size() : 0;
    }

    public void removeAnswer(int pos) {
        if (pos < 0 || pos > answers.size() - 1) {
            return;
        }

        answers.remove(pos);

//        notifyItemRemoved(pos);
//        notifyItemRangeChanged(pos, answers.size() - pos);
        if (onAnswerChanged != null) onAnswerChanged.run();
    }
}
