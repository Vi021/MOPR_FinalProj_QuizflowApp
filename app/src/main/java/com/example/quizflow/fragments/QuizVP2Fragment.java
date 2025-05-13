package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizflow.R;
import com.example.quizflow.adapters.QuizAdapter;
import com.example.quizflow.models.QuizViewModel;
import com.example.quizflow.models.SearchViewModel;

public class QuizVP2Fragment extends Fragment {
    private TextView txt_none;
    private RecyclerView recy_srchQuizzes;
    private QuizAdapter quizAdapter;
    private QuizViewModel quizViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_vp2, container, false);

        txt_none = view.findViewById(R.id.txt_none);

        quizAdapter = new QuizAdapter(requireContext(), null);
        recy_srchQuizzes = view.findViewById(R.id.recy_srchQuizzes);
        recy_srchQuizzes.setAdapter(quizAdapter);
        recy_srchQuizzes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        displayNone();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Observe search query from shared ViewModel
        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                quizViewModel.fetchQuizzes(query.trim());
            }
        });

        // Observe backend results
        quizViewModel.getQuizzes().observe(getViewLifecycleOwner(), quizzes -> {
            quizAdapter.setQuizzes(quizzes);
            displayNone();
        });
    }

    private void displayNone() {
        if (quizAdapter.getItemCount() == 0) {
            txt_none.setVisibility(View.VISIBLE);
            recy_srchQuizzes.setVisibility(View.GONE);
        } else {
            txt_none.setVisibility(View.GONE);
            recy_srchQuizzes.setVisibility(View.VISIBLE);
        }
    }
}