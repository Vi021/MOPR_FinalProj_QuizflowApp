package com.example.quizflow.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizflow.R;
import com.example.quizflow.activities.MainActivity;
import com.example.quizflow.activities.QuestionActivity;
import com.example.quizflow.domains.QuestionModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private CircleImageView circleImg_pfp;
    private ImageView img_coinAdd;
    private TextView txt_hello, txt_viewCategories, txt_startNow;
    private LinearLayout lineL_createQuiz, lineL_singlePlayer, lineL_multiPlayers , lineL_category1, lineL_category2, lineL_category3, lineL_category4;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initFindView(view);

        //txt_hello.setText("Hello, " + "?" + "!");

        view.findViewById(R.id.lineL_singlePlayer).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QuestionActivity.class);
            intent.putParcelableArrayListExtra("list", new ArrayList<>(questionList()));
            startActivity(intent);
        });

        return view;
    }

    private void initFindView(View view) {
        circleImg_pfp = view.findViewById(R.id.circleImg_pfp);
        circleImg_pfp.setOnClickListener(this::noService);

        img_coinAdd = view.findViewById(R.id.img_coinAdd);
        img_coinAdd.setOnClickListener(this::noService);

        txt_hello = view.findViewById(R.id.txt_hello);

        txt_viewCategories = view.findViewById(R.id.txt_viewCategories);
        txt_viewCategories.setOnClickListener(this::noCategories);

        txt_startNow = view.findViewById(R.id.txt_startNow);
        txt_startNow.setOnClickListener(this::noQuizzes);

        lineL_createQuiz = view.findViewById(R.id.lineL_createQuiz);
        lineL_createQuiz.setOnClickListener(this::noService);

        lineL_singlePlayer = view.findViewById(R.id.lineL_singlePlayer);
        lineL_singlePlayer.setOnClickListener(this::noService);

        lineL_multiPlayers = view.findViewById(R.id.lineL_multiPlayers);
        lineL_multiPlayers.setOnClickListener(this::noService);

        lineL_category1 = view.findViewById(R.id.lineL_category1);
        lineL_category1.setOnClickListener(this::noQuizzes);

        lineL_category2 = view.findViewById(R.id.lineL_category2);
        lineL_category2.setOnClickListener(this::noQuizzes);

        lineL_category3 = view.findViewById(R.id.lineL_category3);
        lineL_category3.setOnClickListener(this::noQuizzes);

        lineL_category4 = view.findViewById(R.id.lineL_category4);
        lineL_category4.setOnClickListener(this::noQuizzes);
    }

    // defaults
    public void noCategories(View view) {
        Toast.makeText(requireContext(), "No more categories available!", Toast.LENGTH_SHORT).show();
    }
    public void noService(View view) {
        Toast.makeText(requireContext(), "Service unavailable!", Toast.LENGTH_SHORT).show();
    }
    public void noQuizzes(View view) {
        Toast.makeText(requireContext(), "No quizzes found!", Toast.LENGTH_SHORT).show();
    }

    // Question list data
    private List<QuestionModel> questionList() {
        List<QuestionModel> questions = new ArrayList<>();

        questions.add(new QuestionModel(
                1,
                "Which planet is the largest planet in the solar system?",
                "Earth",
                "Mars",
                "Neptune",
                "Jupiter",
                "d",
                5,
                "q_1",
                null
        ));

        questions.add(new QuestionModel(
                2,
                "Which country is the largest country in the world by land area?",
                "Russia",
                "Canada",
                "United States",
                "China",
                "a",
                5,
                "q_2",
                null
        ));

        questions.add(new QuestionModel(
                3,
                "Which of the following substances is used as an anti-cancer medication in the medical world?",
                "Cheese",
                "Lemon juice",
                "Cannabis",
                "Paspalum",
                "c",
                5,
                "q_3",
                null
        ));

        questions.add(new QuestionModel(
                4,
                "Which moon in the Earth's solar system has an atmosphere?",
                "Luna (Moon)",
                "Phobos (Mars' moon)",
                "Venus' moon",
                "None of the above",
                "d",
                5,
                "q_4",
                null
        ));

        questions.add(new QuestionModel(
                5,
                "Which of the following symbols represents the chemical element with the atomic number 6?",
                "O",
                "H",
                "C",
                "N",
                "c",
                5,
                "q_5",
                null
        ));

        questions.add(new QuestionModel(
                6,
                "Who is credited with inventing theater as we know it today?",
                "Shakespeare",
                "Arthur Miller",
                "Ashkouri",
                "Ancient Greeks",
                "d",
                5,
                "q_6",
                null
        ));

        questions.add(new QuestionModel(
                7,
                "Which ocean is the largest ocean in the world?",
                "Pacific Ocean",
                "Atlantic Ocean",
                "Indian Ocean",
                "Arctic Ocean",
                "a",
                5,
                "q_7",
                null
        ));

        questions.add(new QuestionModel(
                8,
                "Which religions are among the most practiced religions in the world?",
                "Islam, Christianity, Judaism",
                "Buddhism, Hinduism, Sikhism",
                "Zoroastrianism, Brahmanism, Yazdanism",
                "Taoism, Shintoism, Confucianism",
                "a",
                5,
                "q_8",
                null
        ));

        questions.add(new QuestionModel(
                9,
                "In which continent are the most independent countries located?",
                "Asia",
                "Europe",
                "Africa",
                "Americas",
                "c",
                5,
                "q_9",
                null
        ));

        questions.add(new QuestionModel(
                10,
                "Which ocean has the greatest average depth?",
                "Pacific Ocean",
                "Atlantic Ocean",
                "Indian Ocean",
                "Southern Ocean",
                "d",
                5,
                "q_10",
                null
        ));

        return questions;
    }
}