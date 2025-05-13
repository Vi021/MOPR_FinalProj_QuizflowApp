package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizflow.R;
import com.example.quizflow.adapters.CollectionAdapter;
import com.example.quizflow.models.QuizModel;

import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment {
    private RecyclerView recyHistory, recySaved, recyCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        recyHistory = view.findViewById(R.id.recy_history);
        recySaved = view.findViewById(R.id.recy_saved);
        recyCreated = view.findViewById(R.id.recy_created);

        // Thiết lập GridLayoutManager với 2 cột
        recyHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recySaved.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyCreated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Thiết lập Adapter với dữ liệu mẫu
        List<QuizModel> sampleData = getSampleData();
        recyHistory.setAdapter(new CollectionAdapter(getContext(), sampleData));
        recySaved.setAdapter(new CollectionAdapter(getContext(), sampleData));
        recyCreated.setAdapter(new CollectionAdapter(getContext(), sampleData));

        return view;
    }

    // Dữ liệu mẫu
    private List<QuizModel> getSampleData() {
        List<QuizModel> items = new ArrayList<>();
        items.add(new QuizModel(1L, "Math Quiz", "Math description", "Math", true, "01-01-2025", 10, 5400, 1001L, 0, (byte) 1));
        items.add(new QuizModel(2L, "Science Quiz", "Science description", "Science", true, "02-01-2025", 15, 2700, 1002L, 0, (byte) 2));
        items.add(new QuizModel(1L, "Math Quiz", "Math description", "Math", true, "01-01-2025", 10, 5400, 1001L, 0, (byte) 1));
        items.add(new QuizModel(2L, "Science Quiz", "Science description", "Science", true, "02-01-2025", 15, 2700, 1002L, 0, (byte) 2));
        return items;
    }
}