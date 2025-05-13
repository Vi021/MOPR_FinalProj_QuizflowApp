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
import com.example.quizflow.adapters.TopicAdapter;
import com.example.quizflow.models.SearchViewModel;
import com.example.quizflow.models.TopicViewModel;
import com.example.quizflow.utils.TYPE;

public class TopicVP2Fragment extends Fragment {
    private TextView txt_none;
    private RecyclerView recy_srchTopics;
    private TopicAdapter topicAdapter;
    private TopicViewModel topicViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_vp2, container, false);

        txt_none = view.findViewById(R.id.txt_none);

        topicAdapter = new TopicAdapter(requireContext(), TYPE.TOPICS);
        topicAdapter.setLayoutId(R.layout.row_topicitem_search);
        recy_srchTopics = view.findViewById(R.id.recy_srchTopics);
        recy_srchTopics.setAdapter(topicAdapter);
        recy_srchTopics.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        displayNone();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        // Observe search query from shared ViewModel
        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                topicViewModel.fetchTopics(query.trim());
            }
        });

        // Observe backend results
        topicViewModel.getTopics().observe(getViewLifecycleOwner(), topics -> {
            topicAdapter.setCategories(topics);
            displayNone();
        });
    }

    private void displayNone() {
        if (topicAdapter.getItemCount() == 0) {
            txt_none.setVisibility(View.VISIBLE);
            recy_srchTopics.setVisibility(View.GONE);
        } else {
            txt_none.setVisibility(View.GONE);
            recy_srchTopics.setVisibility(View.VISIBLE);
        }
    }
}