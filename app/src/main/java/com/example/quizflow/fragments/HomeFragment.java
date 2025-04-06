package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizflow.R;

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
        Toast toast = Toast.makeText(requireContext(), "No more categories available!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }
    public void noService(View view) {
        Toast toast = Toast.makeText(requireContext(), "Service unavailable!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }
    public void noQuizzes(View view) {
        Toast toast = Toast.makeText(requireContext(), "No quizzes found!", Toast.LENGTH_SHORT);
        toast.show();
        new Handler().postDelayed(toast::cancel, 500);
    }
}