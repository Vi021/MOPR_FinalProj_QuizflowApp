package com.example.quizflow.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.adapters.QuestAdapter2;
import com.example.quizflow.models.AnswerModel;
import com.example.quizflow.models.QuestionModel;
import com.example.quizflow.models.QuizEditorModel;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizEditor2Activity extends AppCompatActivity {
    private Spinner spin_topic;
    private EditText eTxt_hour, eTxt_minute, eTxt_second, eTxt_quizTitle, eTxt_quizDesc;
    private TextView txt_questions;
    private RecyclerView recy_questions;
    private LinearLayout lineL_availability, lineL_addQuestion;
    private Button btn_done;

    private List<QuestionModel> questions = new ArrayList<>();
    private QuestAdapter2 questionAdapter;
    private QuizModel quiz;

    private static int MAX_QUESTION = 30;
    private final boolean[] isPublic = {true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        quiz = (QuizModel) getIntent().getSerializableExtra("quiz");
        if (quiz == null) quiz = new QuizModel();

        if (quiz.getQid() != -1L) {
            loadQuiz();
        } else {
            initViews();
            if (questions.isEmpty()) lineL_addQuestion.performClick();
        }
    }

    private void initViews() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        ImageView img_help = findViewById(R.id.img_help);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img_help.setTooltipText("Go ask ChatGPT");
        } else {
            img_help.setOnClickListener(v -> {
                Toast t = Toast.makeText(this, "Go ask ChatGPT", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1200);
            });
        }

        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(v -> {
            if (validateQuiz()) {
                if (quiz.getQid() == -1L) {
                    createQuiz();
                } else {
                    updateQuiz();
                }
            } else {
                Toast t = Toast.makeText(this, "Please recheck all fields", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1200);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_topicitem_selected, TYPE.Topics);
        adapter.setDropDownViewResource(R.layout.row_topicitem_dropdown);
        spin_topic = findViewById(R.id.spin_topic);
        spin_topic.setAdapter(adapter);
        spin_topic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spin_topic.setSelection(0);
            }
        });
        if (quiz.getTopic() == null) {
            quiz.setTopic(TYPE.TOPICS.get(0));
        }
        spin_topic.setSelection(TYPE.TOPICS.indexOf(quiz.getTopic()));

        eTxt_hour = findViewById(R.id.eTxt_hour);
        eTxt_minute = findViewById(R.id.eTxt_minute);
        eTxt_second = findViewById(R.id.eTxt_second);
        if (quiz.getDuration() > 0) {
            eTxt_hour.setText(String.valueOf(quiz.getDuration() / 3600));
            eTxt_minute.setText(String.valueOf((quiz.getDuration() % 3600) / 60));
            eTxt_second.setText(String.valueOf(quiz.getDuration() % 60));
        }
        LinearLayout lineL_duration = findViewById(R.id.lineL_duration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lineL_duration.setTooltipText("Quiz's duration");
        }

        ImageView img_availability = findViewById(R.id.img_availability);
        TextView txt_availability = findViewById(R.id.txt_availability);
        lineL_availability = findViewById(R.id.lineL_availability);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lineL_availability.setTooltipText("Quiz's availability to everyone");
        }
        lineL_availability.setOnClickListener(v -> {
            isPublic[0] = !isPublic[0];
            if (isPublic[0]) {
                img_availability.setImageResource(R.drawable.ic_globe_white);
                txt_availability.setText("Public");
            } else {
                img_availability.setImageResource(R.drawable.ic_lock_white);
                txt_availability.setText("Private");
            }
        });
        isPublic[0] = quiz.isVisible();
        if (isPublic[0]) {
            img_availability.setImageResource(R.drawable.ic_globe_white);
            txt_availability.setText("Public");
        } else {
            img_availability.setImageResource(R.drawable.ic_lock_white);
            txt_availability.setText("Private");
        }

        eTxt_quizTitle = findViewById(R.id.eTxt_quizTitle);
        eTxt_quizTitle.setText(quiz.getTitle());
        eTxt_quizDesc = findViewById(R.id.eTxt_quizDesc);
        eTxt_quizDesc.setText(quiz.getDescription());
        txt_questions = findViewById(R.id.txt_questions);
        txt_questions.setText("Question(s): " + quiz.getQuestionCount());

        questionAdapter = new QuestAdapter2(this, questions, () -> txt_questions.setText("Questions: " + questions.size()));
        recy_questions = findViewById(R.id.recy_questions);
        recy_questions.setAdapter(questionAdapter);
        recy_questions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        lineL_addQuestion = findViewById(R.id.lineL_addQuestion);
        lineL_addQuestion.setOnClickListener(view -> addQuestion());

        ImageView img_upload = findViewById(R.id.img_upload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img_upload.setTooltipText("Upload a question file");
        }
    }

    private void addQuestion() {
        if (questions.size() >= MAX_QUESTION) {
            Toast t = Toast.makeText(this, "Sorry, but max " + MAX_QUESTION + " questions per quiz!", Toast.LENGTH_SHORT);
            t.show();
            new Handler().postDelayed(t::cancel, 1200);

            return;
        }

        QuestionModel question = new QuestionModel();
        question.setQtid((long) -questions.size() - 1);
        question.setType("MCQ");
        question.setAnswers(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            question.getAnswers().add(new AnswerModel());
        }

        questions.add(question);

        questionAdapter.notifyItemInserted(questions.size());
        //notifyItemChanged(0);

        txt_questions.setText("Question(s): " + (questions != null ? questions.size() : 0));
    }

    private void loadQuiz() {
        try {
            new Retrofit2Client().getAPI().getQuizEditor(quiz.getQid(), Utilities.getUID(this))
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<QuizEditorModel> call, Response<QuizEditorModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                QuizEditorModel quizEditorModel = response.body();
                                quiz = quizEditorModel.getQuiz();
                                questions = quizEditorModel.getQuestions();

                                runOnUiThread(() -> initViews());   // cus Android requires all UI updates to run on the main thread (includes UI thread)

                                Log.i("QF_LOAD_QUIZ", "Quiz loaded: " + quiz.getQid());
                            } else {
                                String msg = "Unknown error";
                                if (response.errorBody() != null) {
                                    try {
                                        msg = response.errorBody().string();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                Utilities.showError(QuizEditor2Activity.this, "QF_LOAD_QUIZ", "Error: " + msg);
                            }
                        }

                        @Override
                        public void onFailure(Call<QuizEditorModel> call, Throwable t) {
                            Utilities.showError(QuizEditor2Activity.this, "QF_LOAD_QUIZ", "Failure: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            Utilities.showError(this, "QF_LOAD_QUIZ", "Exception: " + e.getMessage());
        }
    }

    private boolean validateQuiz() {
        boolean passed = true;

        if (quiz == null) quiz = new QuizModel();

        if (eTxt_quizTitle.getText().toString().isEmpty()) {
            eTxt_quizTitle.setError("Please enter a quiz title");
            passed = false;
        } else {
            eTxt_quizTitle.setError(null);
            quiz.setTitle(eTxt_quizTitle.getText().toString());
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "A quiz must have at least 1 question", Toast.LENGTH_SHORT);
            passed = false;
        }

        for (QuestionModel question : questions) {
            if (question.getQuestion().isEmpty()) {
                Toast t = Toast.makeText(this, "All questions must not be empty", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1200);

                passed = false;
                break;
            }

            boolean hasCorrect = false;
            for (AnswerModel answer : question.getAnswers()) {
                if (answer.getText().isEmpty()) {
                    Toast t = Toast.makeText(this, "All answers of all questions must not be empty", Toast.LENGTH_SHORT);
                    t.show();
                    new Handler().postDelayed(t::cancel, 1200);

                    passed = false;
                    break;
                }

                if (answer.isCorrect()) {
                    hasCorrect = true;
                }
            }

            if (!hasCorrect) {
                Toast t = Toast.makeText(this, "Each question must have a correct answer", Toast.LENGTH_SHORT);
                t.show();
                new Handler().postDelayed(t::cancel, 1200);

                passed = false;
                break;
            }
        }

        if (quiz.getUid() == -1L) {
            // TODO: get user id
        }

        return passed;
    }

    private void createQuiz() {
        quiz.setDescription(eTxt_quizDesc.getText().toString());
        quiz.setTopic(TYPE.TOPICS.get(spin_topic.getSelectedItemPosition()));
        quiz.setDurationFromString(eTxt_hour.getText().toString(), eTxt_minute.getText().toString(), eTxt_second.getText().toString());
        quiz.setVisible(isPublic[0]);
        quiz.setQuestionCount(questions.size());

        QuizEditorModel quizEditorModel = new QuizEditorModel(quiz, QuizEditor2Activity.this.questions);
        new Retrofit2Client().getAPI().createQuiz(quizEditorModel).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long qid = response.body().get("NEW_QID");
                    if (qid != null) {
                        quiz.setQid(qid);

                        Toast t = Toast.makeText(QuizEditor2Activity.this, "Quiz created!", Toast.LENGTH_SHORT);
                        t.show();
                        new Handler().postDelayed(t::cancel, 1200);
                    } else {
                        Utilities.showError(QuizEditor2Activity.this, "QF_SAVE_QUIZ", "Error: Quiz ID is null");
                    }
                } else {
                    String msg = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            msg = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Utilities.showError(QuizEditor2Activity.this, "QF_SAVE_QUIZ", "Error: " + msg);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                Utilities.showError(QuizEditor2Activity.this, "QF_SAVE_QUIZ", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        quiz = null;
        super.onBackPressed();
    }

    private void updateQuiz() {
        if (quiz.getQid() == null || quiz.getQid() == -1L) {
            Toast t = Toast.makeText(this, "Quiz not found", Toast.LENGTH_SHORT);
            t.show();
            new Handler().postDelayed(t::cancel, 1200);

            return;
        }

        quiz.setDescription(eTxt_quizDesc.getText().toString());
        quiz.setTopic(TYPE.TOPICS.get(spin_topic.getSelectedItemPosition()));
        quiz.setDurationFromString(eTxt_hour.getText().toString(), eTxt_minute.getText().toString(), eTxt_second.getText().toString());
        quiz.setVisible(isPublic[0]);
        quiz.setQuestionCount(questions.size());

        QuizEditorModel quizEditorModel = new QuizEditorModel(quiz, QuizEditor2Activity.this.questions);
        new Retrofit2Client().getAPI().updateQuiz(quizEditorModel).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast t = Toast.makeText(QuizEditor2Activity.this, "Quiz updated!", Toast.LENGTH_SHORT);
                    t.show();
                    new Handler().postDelayed(t::cancel, 1200);
                } else {
                    String msg = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            msg = response.errorBody().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Utilities.showError(QuizEditor2Activity.this, "QF_UPDATE_QUIZ", "Error: " + msg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utilities.showError(QuizEditor2Activity.this, "QF_UPDATE_QUIZ", "Failure: " + t.getMessage());
            }
        });
    }
}