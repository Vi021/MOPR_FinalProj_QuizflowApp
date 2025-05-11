package com.example.quizflow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<UserModel> rankings;

    public RankingAdapter(List<UserModel> rankings) {
        this.rankings = rankings;
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView txt_rank, txt_username, txt_coinCount;
        CircleImageView cImg_pfp;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_rank = itemView.findViewById(R.id.txt_rank);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_coinCount = itemView.findViewById(R.id.txt_coinCount);
            cImg_pfp = itemView.findViewById(R.id.cImg_pfp);
        }
    }

    @NonNull
    @Override
    public RankingAdapter.RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rankingitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.RankingViewHolder holder, int position) {
        if (rankings != null && position < rankings.size()) {
            UserModel user = rankings.get(position);
            holder.txt_rank.setText("#" + (position+4));    // from 4th rank downwards
            holder.txt_username.setText(user.getUsername());
            holder.cImg_pfp.setImageResource(R.drawable.ic_default_profile_icebear); // TODO: glide with context!
            holder.txt_coinCount.setText(String.valueOf(user.getCoins()));
        }
    }

    @Override
    public int getItemCount() {
        return rankings != null ? rankings.size() : 0;
    }
}
