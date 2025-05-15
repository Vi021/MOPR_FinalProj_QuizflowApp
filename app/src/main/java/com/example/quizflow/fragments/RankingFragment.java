package com.example.quizflow.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.adapters.RankingAdapter;
import com.example.quizflow.models.UserModel;
import com.example.quizflow.respones.RankingResponse;
import com.example.quizflow.utils.Refs;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {
    private static final String TAG = "RankingFragment";
    private TextView txt_usernameTop1, txt_usernameTop2, txt_usernameTop3,
            txt_coinsTop1, txt_coinsTop2, txt_coinsTop3;
    private CircleImageView cirImg_pfpTop1, cirImg_pfpTop2, cirImg_pfpTop3;
    private AppCompatButton ACBtn_weekly, ACBtn_monthly, ACBtn_allTime;
    private RecyclerView recycler_rankings;
    private ArrayList<UserModel> rankings;
    private String currentPeriod = "all"; // Default period

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        initViews(view);
        fetchRankings(currentPeriod);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            fetchRankings(currentPeriod);
        }
    }

    private void initViews(View view) {
        txt_usernameTop1 = view.findViewById(R.id.txt_usernameTop1);
        txt_usernameTop2 = view.findViewById(R.id.txt_usernameTop2);
        txt_usernameTop3 = view.findViewById(R.id.txt_usernameTop3);
        txt_coinsTop1 = view.findViewById(R.id.txt_coinsTop1);
        txt_coinsTop2 = view.findViewById(R.id.txt_coinsTop2);
        txt_coinsTop3 = view.findViewById(R.id.txt_coinsTop3);
        cirImg_pfpTop1 = view.findViewById(R.id.cirImg_pfpTop1);
        cirImg_pfpTop2 = view.findViewById(R.id.cirImg_pfpTop2);
        cirImg_pfpTop3 = view.findViewById(R.id.cirImg_pfpTop3);
        ACBtn_weekly = view.findViewById(R.id.ACBtn_weekly);
        ACBtn_monthly = view.findViewById(R.id.ACBtn_monthly);
        ACBtn_allTime = view.findViewById(R.id.ACBtn_allTime);
        recycler_rankings = view.findViewById(R.id.recycler_rankings);

        ACBtn_weekly.setOnClickListener(v -> {
            Log.d(TAG, "Weekly button clicked");
            updateButtonStyles(ACBtn_weekly);
            currentPeriod = "weekly";
            fetchRankings(currentPeriod);
        });
        
        ACBtn_monthly.setOnClickListener(v -> {
            Log.d(TAG, "Monthly button clicked");
            updateButtonStyles(ACBtn_monthly);
            currentPeriod = "monthly";
            fetchRankings(currentPeriod);
        });
        
        ACBtn_allTime.setOnClickListener(v -> {
            Log.d(TAG, "All-time button clicked");
            updateButtonStyles(ACBtn_allTime);
            currentPeriod = "all";
            fetchRankings(currentPeriod);
        });
        
        // Default button styling
        updateButtonStyles(ACBtn_allTime);
    }

    private void updateButtonStyles(AppCompatButton selectedButton) {
        // Reset all buttons
        ACBtn_weekly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
        ACBtn_weekly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
        ACBtn_monthly.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
        ACBtn_monthly.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
        ACBtn_allTime.setBackgroundResource(R.drawable.rounded_bg_white_clickable);
        ACBtn_allTime.setTextColor(requireContext().getColor(R.color.xanh_ngoc));
        
        // Set selected button style
        selectedButton.setBackgroundResource(R.drawable.blue_bg);
        selectedButton.setTextColor(requireContext().getColor(R.color.white));
    }

    private void fetchRankings(String period) {
        Log.d(TAG, "Fetching rankings for period: " + period);
        
        Retrofit2Client.getAPI()
                .getRankings(period)
                .enqueue(new Callback<List<RankingResponse>>() {
                    @Override
                    public void onResponse(Call<List<RankingResponse>> call, Response<List<RankingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<RankingResponse> rankingList = response.body();
                            Log.d(TAG, "Rankings fetched successfully, count: " + rankingList.size());
                            processRankingData(rankingList);
                        } else {
                            Log.e(TAG, "Error fetching rankings: " + response.code() + " - " + response.message());
                            Toast.makeText(getContext(), "Không thể tải dữ liệu bảng xếp hạng: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RankingResponse>> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch rankings", t);
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processRankingData(List<RankingResponse> rankingResponses) {
        if (rankingResponses == null || rankingResponses.isEmpty()) {
            Toast.makeText(getContext(), "Không có dữ liệu xếp hạng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to UserModel list
        rankings = new ArrayList<>();
        for (RankingResponse response : rankingResponses) {
            UserModel user = new UserModel(
                    response.getId(),
                    response.getUsername(),
                    response.getImage(),
                    response.getCoins()
            );
            rankings.add(user);
        }

        // Setup UI with data
        setupTop3();
        setupRecyclerView();
    }

    private void setupTop3() {
        if (rankings == null || rankings.isEmpty()) {
            return;
        }

        UserModel user;

        // Handle case where we have less than 3 users
        int size = rankings.size();

        // rank #1
        if (size > 0) {
            user = rankings.get(0);
            txt_usernameTop1.setText(user.getUsername());
            txt_coinsTop1.setText(String.valueOf(user.getCoins()));
            loadProfileImage(cirImg_pfpTop1, user.getPfp());
        }

        // rank #2
        if (size > 1) {
            user = rankings.get(1);
            txt_usernameTop2.setText(user.getUsername());
            txt_coinsTop2.setText(String.valueOf(user.getCoins()));
            loadProfileImage(cirImg_pfpTop2, user.getPfp());
        } else {
            txt_usernameTop2.setText("---");
            txt_coinsTop2.setText("0");
            cirImg_pfpTop2.setImageResource(R.drawable.ic_default_pfp_icebear);
        }

        // rank #3
        if (size > 2) {
            user = rankings.get(2);
            txt_usernameTop3.setText(user.getUsername());
            txt_coinsTop3.setText(String.valueOf(user.getCoins()));
            loadProfileImage(cirImg_pfpTop3, user.getPfp());
        } else {
            txt_usernameTop3.setText("---");
            txt_coinsTop3.setText("0");
            cirImg_pfpTop3.setImageResource(R.drawable.ic_default_pfp_icebear);
        }
    }

    private void loadProfileImage(CircleImageView imageView, String imagePath) {
        if (getContext() == null) return;
        
        if (imagePath != null && !imagePath.isEmpty()) {
            String imageUrl = Refs.BASE_IMAGE_URL + imagePath;
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_pfp_icebear)
                    .error(R.drawable.ic_default_pfp_icebear)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_default_pfp_icebear);
        }
    }

    private void setupRecyclerView() {
        if (rankings == null || rankings.size() <= 3) {
            // No users beyond the top 3
            recycler_rankings.setAdapter(new RankingAdapter(new ArrayList<>()));
            return;
        }

        RankingAdapter rankingAdapter = new RankingAdapter(rankings.subList(3, rankings.size())); // from 4th rank downwards
        recycler_rankings.setAdapter(rankingAdapter);
        recycler_rankings.setHasFixedSize(true);
        recycler_rankings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}
