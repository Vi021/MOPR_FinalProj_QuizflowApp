package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizflow.R;
import com.example.quizflow.adapters.RankingAdapter;
import com.example.quizflow.models.UserModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class RankingFragment extends Fragment {
    AppCompatButton ACBtn_weekly, ACBtn_monthly, ACBtn_allTime;
    RecyclerView recycler_rankings;
    ArrayList<UserModel> rankings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        initViews(view);
        exampleData();

        // rank #1,2,3
        setupTop3(view);
        // rank #4 and downwards
        setupRecyclerView();

        // TODO: Ranking by week, month, all time

        return view;
    }

    private void initViews(View view) {
        ACBtn_weekly = view.findViewById(R.id.ACBtn_weekly);
        ACBtn_monthly = view.findViewById(R.id.ACBtn_monthly);
        ACBtn_allTime = view.findViewById(R.id.ACBtn_allTime);
        recycler_rankings = view.findViewById(R.id.recycler_rankings);

        ACBtn_weekly.setOnClickListener(v -> {
            ACBtn_weekly.setBackgroundResource(R.drawable.blue_bg);
            ACBtn_weekly.setTextColor(requireContext().getColor(R.color.white));
            ACBtn_monthly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_monthly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
            ACBtn_allTime.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_allTime.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
        });
        ACBtn_monthly.setOnClickListener(v -> {
            ACBtn_weekly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_weekly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
            ACBtn_monthly.setBackgroundResource(R.drawable.blue_bg);
            ACBtn_monthly.setTextColor(requireContext().getColor(R.color.white));
            ACBtn_allTime.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_allTime.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
        });
        ACBtn_allTime.setOnClickListener(v -> {
            ACBtn_weekly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_weekly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
            ACBtn_monthly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
            ACBtn_monthly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
            ACBtn_allTime.setBackgroundResource(R.drawable.blue_bg);
            ACBtn_allTime.setTextColor(requireContext().getColor(R.color.white));
        });
    }

    private void exampleData() {
        rankings = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 11; i++) {
            int randomCoins = random.nextInt(10000); // generates 0–9999
            String username = "User" + i;
            String pfp = "pfp" + i;

            rankings.add(new UserModel(i, username, pfp, randomCoins));
        }

        rankings.sort(Comparator.comparingLong(UserModel::getCoins).reversed());
    }

    private void setupTop3(View view) {
        int[] usernameIds = { R.id.txt_usernameTop1, R.id.txt_usernameTop2, R.id.txt_usernameTop3 };
        int[] coinsIds = { R.id.txt_coinsTop1, R.id.txt_coinsTop2, R.id.txt_coinsTop3 };
        int[] pfpIds = { R.id.img_pfpTop1, R.id.img_pfpTop2, R.id.img_pfpTop3 };

        for (int i = 0; i < 3; i++) {
            UserModel user = rankings.get(i);

            TextView txtUsername = view.findViewById(usernameIds[i]);
            TextView txtCoins = view.findViewById(coinsIds[i]);
            ShapeableImageView imgPfp = view.findViewById(pfpIds[i]);

            txtUsername.setText(user.getUsername());
            txtCoins.setText(String.valueOf(user.getCoins()));
            imgPfp.setImageResource(R.drawable.ic_default_pfp_blues); // TODO: glide with context!
        }
    }

    private void setupRecyclerView() {
        RankingAdapter rankingAdapter = new RankingAdapter(rankings.subList(3, rankings.size())); // from 4th rank downwards
        recycler_rankings.setAdapter(rankingAdapter);
        recycler_rankings.setHasFixedSize(true);
        recycler_rankings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}
