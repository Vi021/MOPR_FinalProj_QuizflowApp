package com.example.quizflow.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.models.UserModel;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<UserModel> users;

    public RankingAdapter(List<UserModel> userList) {
        this.users = userList;
    }

    class RankingViewHolder extends RecyclerView.ViewHolder {

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RankingAdapter.RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.RankingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
