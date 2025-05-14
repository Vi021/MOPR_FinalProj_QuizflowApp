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
import com.example.quizflow.adapters.UserAdapter;
import com.example.quizflow.models.SearchViewModel;
import com.example.quizflow.models.UserViewModel;

public class UserVP2Fragment extends Fragment {
    private TextView txt_none;
    private RecyclerView recy_srchUsers;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_vp2, container, false);

        txt_none = view.findViewById(R.id.txt_none);

        userAdapter = new UserAdapter(requireContext(), null);
        recy_srchUsers = view.findViewById(R.id.recy_srchUsers);
        recy_srchUsers.setAdapter(userAdapter);
        recy_srchUsers.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        displayNone();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                userViewModel.fetchUsers(query.trim());
            }
        });

        userViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            userAdapter.setUsers(users);
            displayNone();
        });
    }

    private void displayNone() {
        if (userAdapter.getItemCount() == 0) {
            txt_none.setVisibility(View.VISIBLE);
            recy_srchUsers.setVisibility(View.GONE);
        } else {
            txt_none.setVisibility(View.GONE);
            recy_srchUsers.setVisibility(View.VISIBLE);
        }
    }
}