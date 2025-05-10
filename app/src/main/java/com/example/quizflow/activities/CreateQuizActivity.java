package com.example.quizflow.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.adapters.QuestAdapter;
import com.example.quizflow.models.AnswerModel;
import com.example.quizflow.models.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {
    private EditText eTxt_quizTitle, eTxt_quizDesc;
    private TextView txt_questions;
    private RecyclerView recy_questions;
    private LinearLayout lineL_addQuestion;
    private Button btn_done;

    private List<QuestionModel> questions = new ArrayList<>();
    private QuestAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        initViews();
        if (questions.isEmpty()) lineL_addQuestion.performClick();
    }

    private void initViews() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> getOnBackPressedDispatcher());

        ImageView img_help = findViewById(R.id.img_help);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img_help.setTooltipText("Go ask ChatGPT");
        } else {
            img_help.setOnClickListener(v -> {
                Toast t = Toast.makeText(this, "Go ask ChatGPT", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1000);
            });
        }

        btn_done = findViewById(R.id.btn_done);

        eTxt_quizTitle = findViewById(R.id.eTxt_quizTitle);
        eTxt_quizDesc = findViewById(R.id.eTxt_quizDesc);
        txt_questions = findViewById(R.id.txt_questions);

        questionAdapter = new QuestAdapter(this, questions, () -> txt_questions.setText("Questions: " + questions.size()));
        recy_questions = findViewById(R.id.recy_questions);
        recy_questions.setAdapter(questionAdapter);
        recy_questions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        lineL_addQuestion = findViewById(R.id.lineL_addQuestion);
        lineL_addQuestion.setOnClickListener(view -> addQuestion());

        ImageView img_upload = findViewById(R.id.img_upload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img_upload.setTooltipText("Upload a question file");
        }
        //img_upload.setOnClickListener(view -> uploadQuestionFile());
    }

    private void addQuestion() {
        if (questions.size() >= 59) {
            Toast t = Toast.makeText(this, "Sorry, but max 60 questions per quiz!", Toast.LENGTH_SHORT);
            t.show();
            new Handler().postDelayed(t::cancel, 1200);

            return;
        }

        QuestionModel question = new QuestionModel();
        question.setQtid((long) questions.size() + 1);  //temp
        question.setType("MCQ");
        question.setAnswers(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            question.getAnswers().add(new AnswerModel());
        }

        questions.add(question);
        questionAdapter.notifyItemInserted(questions.size() - 1);
        questionAdapter.notifyItemChanged(0);
        txt_questions.setText("Question(s): " + (questions != null ? questions.size() : 0));
    }
}