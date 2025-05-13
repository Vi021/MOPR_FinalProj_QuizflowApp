package com.example.quizflow.adapters;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.models.AnswerModel;
import com.example.quizflow.models.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestVewHolder> {
    private Context context;
    private List<QuestionModel> questions;
    private Runnable onQuestionChanged;

    private static int MAX_ANSWER = 4;

    public QuestAdapter(Context context, List<QuestionModel> questions) {
        this.context = context;
        this.questions = questions;
    }

    public QuestAdapter(Context context, List<QuestionModel> questions, Runnable onQuestionChanged) {
        this.context = context;
        this.questions = questions;
        this.onQuestionChanged = onQuestionChanged;
    }

    public class QuestVewHolder extends RecyclerView.ViewHolder {
        private TextView txt_questionNum;
        private EditText eTxt_question;
        private ImageView img_mcq, img_tf, img_shortAns, img_up, img_down, img_remove;
        private RecyclerView recy_answers;
        private LinearLayout lineL_addAns;
        private AnswerAdapter adapter;

        public QuestVewHolder(@NonNull View itemView) {
            super(itemView);
            txt_questionNum = itemView.findViewById(R.id.txt_questionNum);
            eTxt_question = itemView.findViewById(R.id.eTxt_question);
            img_mcq = itemView.findViewById(R.id.img_mcq);
            img_tf = itemView.findViewById(R.id.img_tf);
            img_shortAns = itemView.findViewById(R.id.img_shortAns);
            img_up = itemView.findViewById(R.id.img_up);
            img_down = itemView.findViewById(R.id.img_down);
            img_remove = itemView.findViewById(R.id.img_remove);
            recy_answers = itemView.findViewById(R.id.recy_answers);
            lineL_addAns = itemView.findViewById(R.id.lineL_addAns);

            recy_answers.setLayoutManager(new LinearLayoutManager(itemView.getContext(), RecyclerView.VERTICAL, false));
        }
    }

    @NonNull
    @Override
    public QuestVewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestVewHolder(LayoutInflater.from(context).inflate(R.layout.row_questionitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestVewHolder holder, int position) {
        QuestionModel question = questions.get(position);
        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        } else if (question.getAnswers().isEmpty()) {
            question.getAnswers().add(new AnswerModel());
        }
        AnswerAdapter answerAdapter = new AnswerAdapter(context, question.getAnswers(), () -> this.notifyItemChanged(position));

        holder.txt_questionNum.setText("Q." + question.getQtid());

        holder.eTxt_question.setText(question.getQuestion());
        if (holder.eTxt_question.getTag() instanceof TextWatcher) {
            holder.eTxt_question.removeTextChangedListener((TextWatcher) holder.eTxt_question.getTag());
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO: no empty question
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                question.setQuestion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO: no empty question
            }
        };
        holder.eTxt_question.addTextChangedListener(watcher);

        holder.img_mcq.setOnClickListener(v -> {
            switchQuestionType(position, "MCQ", answerAdapter);
        });
        holder.img_tf.setOnClickListener(v -> {
            switchQuestionType(position, "TF", answerAdapter);
        });
//        holder.img_shortAns.setOnClickListener(v -> {
//            switchQuestionType(position, "SA", answerAdapter);
//        });
        if (question.getAnswers() != null) {
            holder.img_mcq.setBackgroundTintList(context.getColorStateList(android.R.color.transparent));
            holder.img_tf.setBackgroundTintList(context.getColorStateList(android.R.color.transparent));
//            holder.img_shortAns.setBackgroundTintList(context.getColorStateList(android.R.color.transparent));

            if (question.getAnswers().size() >= 3) {
                question.setType("MCQ");
                holder.img_mcq.setBackgroundTintList(context.getColorStateList(R.color.xanh_botro1));
            } else if (question.getAnswers().size() == 2) {
                question.setType("TF");
                holder.img_tf.setBackgroundTintList(context.getColorStateList(R.color.xanh_botro1));
//            } else if (question.getAnswers().size() == 1) {
//                question.setType("SA");
//                holder.img_shortAns.setBackgroundTintList(context.getColorStateList(R.color.xanh_botro1));
            }
        }

        holder.img_up.setEnabled(position != 0);
        holder.img_up.setOnClickListener(v -> moveQuestion(position, position - 1));

        holder.img_remove.setEnabled(questions.size() > 1);
        holder.img_remove.setOnClickListener(v -> removeQuestion(position));

        holder.img_down.setEnabled(position != questions.size() - 1);
        holder.img_down.setOnClickListener(v -> moveQuestion(position, position + 1));

        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        }

        holder.recy_answers.setAdapter(answerAdapter);

        holder.lineL_addAns.setEnabled(question.getAnswers().size() < MAX_ANSWER);
        holder.lineL_addAns.setOnClickListener(v -> {
            AnswerModel newAnswer = new AnswerModel();
            //newAnswer.setQtid(question.getQtid());    // nah do later
            newAnswer.setCorrect(false);
            question.getAnswers().add(newAnswer);

            holder.lineL_addAns.setEnabled(question.getAnswers().size() < MAX_ANSWER);
            this.notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    public void moveQuestion(int fromPos, int toPos) {
        if (fromPos < 0 || fromPos >= questions.size() || toPos < 0 || toPos >= questions.size()) {
            return;
        }

        QuestionModel question = questions.remove(fromPos);
        question.setQtid((long) toPos + 1);
        questions.add(toPos, question);
        questions.get(fromPos).setQtid((long) fromPos + 1);

        notifyItemMoved(fromPos, toPos);
        notifyItemChanged(fromPos);
        notifyItemChanged(toPos);
    }

    public void removeQuestion(int pos) {
        if (pos < 0 || pos > questions.size() - 1) return;

        questions.remove(pos);

        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, questions.size() - pos);
        notifyItemChanged(0);

        if (onQuestionChanged != null) onQuestionChanged.run();
    }

    public void switchQuestionType(int pos, String type, AnswerAdapter answerAdapter) {
        QuestionModel question = questions.get(pos);
        if (question.getType().equals(type)) return;

        int desiredSize = 0;
        switch (type) {
            case "MCQ":
                desiredSize = 4;
                break;
            case "TF":
                desiredSize = 2;
                break;
            case "SA":
                desiredSize = 1;
                break;
            default:
                throw new IllegalArgumentException("Unsupported question type: " + type);
        }

        int oldSize = question.getAnswers().size();
        for (int i = oldSize; i < desiredSize; i++) {
            AnswerModel answer = new AnswerModel();
            //answer.setQtid(question.getQtid());   // nah do later
            answer.setCorrect(type.equals("SA"));   // SA → correct=true
            question.getAnswers().add(answer);
        }
        if (desiredSize > oldSize) {
            answerAdapter.notifyItemRangeInserted(oldSize, desiredSize - oldSize);
        }

        // REMOVE answers if needed
        int removeCount = question.getAnswers().size() - desiredSize;
        for (int i = 0; i < removeCount; i++) {
            question.getAnswers().remove(question.getAnswers().size() - 1);
        }
        if (removeCount > 0) {
            answerAdapter.notifyItemRangeRemoved(desiredSize, removeCount);
        }

        question.setType(type);
        this.notifyItemChanged(pos);
    }
}
