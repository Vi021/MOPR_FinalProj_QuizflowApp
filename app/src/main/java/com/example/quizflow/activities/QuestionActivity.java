package com.example.quizflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.quizflow.R;
import com.example.quizflow.adapter.QuestionAdapter;
import com.example.quizflow.databinding.ActivityQuestionBinding;
import com.example.quizflow.domains.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity implements QuestionAdapter.Score {

    private ActivityQuestionBinding binding;
    private int position = 0;
    private List<QuestionModel> receivedList = new ArrayList<>();
    private int allScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set padding for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        receivedList = getIntent().getParcelableArrayListExtra("list");
        if (receivedList == null) {
            receivedList = new ArrayList<>();
        }

        binding.backBtn.setOnClickListener(v -> finish());
        binding.progressBar.setProgress(1);
        binding.questionTxt.setText(receivedList.get(position).getQuestion());

        int drawableResourceId = getResources().getIdentifier(
                receivedList.get(position).getPicPath(),
                "drawable",
                getPackageName()
        );

        Glide.with(this)
                .load(drawableResourceId)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                .into(binding.pic);

        loadAnswers();

        binding.rightArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == 10) {
                Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
                intent.putExtra("Score", allScore);
                startActivity(intent);
                finish();
                return;
            }

            position++;
            binding.progressBar.setProgress(binding.progressBar.getProgress() + 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/10");
            binding.questionTxt.setText(receivedList.get(position).getQuestion());

            int drawableResId = getResources().getIdentifier(
                    receivedList.get(position).getPicPath(),
                    "drawable",
                    getPackageName()
            );

            Glide.with(this)
                    .load(drawableResId)
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                    .into(binding.pic);

            loadAnswers();
        });

        binding.leftArrow.setOnClickListener(v -> {
            if (binding.progressBar.getProgress() == 1) return;

            position--;
            binding.progressBar.setProgress(binding.progressBar.getProgress() - 1);
            binding.questionNumberTxt.setText("Question " + binding.progressBar.getProgress() + "/10");
            binding.questionTxt.setText(receivedList.get(position).getQuestion());

            int drawableResId = getResources().getIdentifier(
                    receivedList.get(position).getPicPath(),
                    "drawable",
                    getPackageName()
            );

            Glide.with(this)
                    .load(drawableResId)
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                    .into(binding.pic);

            loadAnswers();
        });
    }

    private void loadAnswers() {
        List<String> users = new ArrayList<>();
        users.add(String.valueOf(receivedList.get(position).getAnswer1()));
        users.add(String.valueOf(receivedList.get(position).getAnswer2()));
        users.add(String.valueOf(receivedList.get(position).getAnswer3()));
        users.add(String.valueOf(receivedList.get(position).getAnswer4()));

        if (receivedList.get(position).getClickedAnswer() != null) {
            users.add(String.valueOf(receivedList.get(position).getClickedAnswer()));
        }

        QuestionAdapter questionAdapter = new QuestionAdapter(
                String.valueOf(receivedList.get(position).getCorrectAnswer()),
                users,
                this
        );

        questionAdapter.differ.submitList(users);
        binding.questionList.setLayoutManager(new LinearLayoutManager(this));
        binding.questionList.setAdapter(questionAdapter);
    }

    @Override
    public void amount(int number, String clickedAnswer) {
        allScore += number;
        receivedList.get(position).setClickedAnswer(clickedAnswer);
    }
}
