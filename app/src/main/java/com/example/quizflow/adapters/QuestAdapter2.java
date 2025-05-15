package com.example.quizflow.adapters;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.models.AnswerModel;
import com.example.quizflow.models.QuestionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<QuestionModel> questions;
    private final Runnable onQuestionChanged;

    private final String type = "MCQ";    // default

    public QuestAdapter2(Context context, List<QuestionModel> questions, Runnable onQuestionChanged) {
        this.context = context;
        this.questions = questions;
        this.onQuestionChanged = onQuestionChanged;
    }

    public class MCQViewHolder extends RecyclerView.ViewHolder {
        private final EditText eTxt_question;
        //        private final EditText eTxt_ans1;
//        private final EditText eTxt_ans2;
//        private final EditText eTxt_ans3;
//        private final EditText eTxt_ans4;
        private final ImageView img_tf;
//        private final ImageView img_remove;
        private final RadioGroup radGr_isCorrect;

        public MCQViewHolder(@NonNull View itemView) {
            super(itemView);
            this.eTxt_question = itemView.findViewById(com.example.quizflow.R.id.eTxt_question);
//            this.eTxt_ans1 = itemView.findViewById(R.id.eTxt_ans1);
//            this.eTxt_ans2 = itemView.findViewById(R.id.eTxt_ans2);
//            this.eTxt_ans3 = itemView.findViewById(R.id.eTxt_ans3);
//            this.eTxt_ans4 = itemView.findViewById(R.id.eTxt_ans4);
            this.img_tf = itemView.findViewById(R.id.img_tf);
//            this.img_remove = itemView.findViewById(R.id.img_remove);
            this.radGr_isCorrect = itemView.findViewById(R.id.radGr_isCorrect);
        }
    }

    public class TFViewHolder extends RecyclerView.ViewHolder {
        private final EditText eTxt_question;
        //        private final EditText eTxt_ans1;
//        private final EditText eTxt_ans2;
        private final ImageView img_mcq;
//        private final ImageView img_remove;
        private final RadioGroup radGr_isCorrect;

        public TFViewHolder(@NonNull View itemView) {
            super(itemView);
            this.eTxt_question = itemView.findViewById(com.example.quizflow.R.id.eTxt_question);
//            this.eTxt_ans1 = itemView.findViewById(R.id.eTxt_ans1);
//            this.eTxt_ans2 = itemView.findViewById(R.id.eTxt_ans2);
            this.img_mcq = itemView.findViewById(R.id.img_mcq);
//            this.img_remove = itemView.findViewById(R.id.img_remove);
            this.radGr_isCorrect = itemView.findViewById(R.id.radGr_isCorrect);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = questions.get(position).getType();
        if (type.equals("TF")) {
            return 1; // TF
        } else {
            return 0; // MCQ
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new TFViewHolder(LayoutInflater.from(context).inflate(R.layout.row_questionitem_tf, parent, false));
        }
        return new MCQViewHolder(LayoutInflater.from(context).inflate(R.layout.row_questionitem_mcq, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuestionModel question = questions.get(position);
        int type = getItemViewType(position);
        List<Integer> ansIds = Arrays.asList(R.id.eTxt_ans1, R.id.eTxt_ans2, R.id.eTxt_ans3, R.id.eTxt_ans4);
        List<Integer> radCorrectIds = Arrays.asList(R.id.radBtn_ans1, R.id.radBtn_ans2, R.id.radBtn_ans3, R.id.radBtn_ans4);
        int currentPosition = holder.getAdapterPosition();

        TextView txt_questionNum = holder.itemView.findViewById(R.id.txt_questionNum);
        txt_questionNum.setText("Q." + (currentPosition+1));

        EditText eTxt_question = holder.itemView.findViewById(R.id.eTxt_question);
        eTxt_question.setText(question.getQuestion());
        if (eTxt_question.getTag() instanceof TextWatcher) {
            eTxt_question.removeTextChangedListener((TextWatcher) eTxt_question.getTag());
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                question.setQuestion(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        eTxt_question.addTextChangedListener(watcher);

        if (type == 0 && holder instanceof MCQViewHolder) {
            MCQViewHolder mcq = (MCQViewHolder) holder;

            if (question.getAnswers() == null) {
                question.setAnswers(new ArrayList<>());
            }

            int ansSize = Math.min(question.getAnswers().size(), 4);
            for (int i = 0; i < ansSize; i++) {
                EditText editText = mcq.itemView.findViewById(ansIds.get(i));
                RadioButton radioButton = mcq.itemView.findViewById(radCorrectIds.get(i));
                extracted1(question, i, editText, radioButton);
            }

            mcq.img_tf.setOnClickListener(v -> switchQuestionType(currentPosition, "TF"));

            mcq.radGr_isCorrect.setOnCheckedChangeListener((correctGroup, checkedId) -> {
                RadioButton selected = mcq.itemView.findViewById(checkedId);
                int i = correctGroup.indexOfChild(selected);
                for (int a = 0; a < question.getAnswers().size(); a++) {
                    question.getAnswers().get(a).setCorrect(a == i);
                }
            });
        } else if (type == 1 && holder instanceof TFViewHolder) {
            TFViewHolder tf = (TFViewHolder) holder;

            if (question.getAnswers() == null) {
                question.setAnswers(new ArrayList<>());
            }

            int ansSize = Math.min(question.getAnswers().size(), 2);
            for (int i = 0; i < ansSize; i++) {
                EditText editText = tf.itemView.findViewById(ansIds.get(i));
                RadioButton radioButton = tf.itemView.findViewById(radCorrectIds.get(i));
                extracted1(question, i, editText, radioButton);
            }

            tf.img_mcq.setOnClickListener(v -> switchQuestionType(currentPosition, "MCQ"));

            tf.radGr_isCorrect.setOnCheckedChangeListener((correctGroup, checkedId) -> {
                RadioButton selected = tf.itemView.findViewById(checkedId);
                int i = correctGroup.indexOfChild(selected);
                for (int a = 0; a < question.getAnswers().size(); a++) {
                    question.getAnswers().get(a).setCorrect(a == i);
                }
            });
        } else {
            Toast t = Toast.makeText(context, "Unable to get item at pos: " + position, Toast.LENGTH_SHORT);
            t.show();
            new Handler().postDelayed(t::cancel, 1200);
        }

        ImageView img_remove = holder.itemView.findViewById(R.id.img_remove);
        img_remove.setEnabled(questions.size() > 1);
        img_remove.setOnClickListener(v -> {
            if (currentPosition > 0) {
                removeQuestion(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    public void switchQuestionType(int pos, String type) {
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
            default:
                throw new IllegalArgumentException("Unsupported question type: " + type);
        }

        int oldSize = question.getAnswers().size();
        for (int i = oldSize; i < desiredSize; i++) {
            AnswerModel answer = new AnswerModel();
            question.getAnswers().add(answer);
        }

        // REMOVE answers if needed
        int removeCount = question.getAnswers().size() - desiredSize;
        for (int i = 0; i < removeCount; i++) {
            question.getAnswers().remove(question.getAnswers().size() - 1);
        }

        question.setType(type);
        notifyItemChanged(pos);
    }

    private static void extracted1(QuestionModel question, int i, EditText editText, RadioButton radBtn_isCorrect) {
        AnswerModel answer =  question.getAnswers().get(i);

        editText.setText(answer.getText());
        if (editText.getTag() instanceof TextWatcher) {
            editText.removeTextChangedListener((TextWatcher) editText.getTag());
        }
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                answer.setText(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
        editText.addTextChangedListener(tw);

        radBtn_isCorrect.setChecked(answer.isCorrect());
    }

    private void removeQuestion(int pos) {
        if (pos < 0 || pos > questions.size() - 1) return;

        questions.remove(pos);

        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, questions.size() - pos);
        //notifyItemChanged(0);

        if (onQuestionChanged != null) onQuestionChanged.run();
    }
}
