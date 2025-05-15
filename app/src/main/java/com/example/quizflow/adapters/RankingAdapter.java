package com.example.quizflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.models.UserModel;
import com.example.quizflow.utils.Refs;

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
            holder.txt_coinCount.setText(String.valueOf(user.getCoins()));
            loadProfileImage(holder.cImg_pfp, user.getPfp(), holder.itemView.getContext());
        }
    }
    
    private void loadProfileImage(CircleImageView imageView, String imagePath, Context context) {
        if (imagePath != null && !imagePath.isEmpty()) {
            String imageUrl = Refs.BASE_URL + "uploads/" + imagePath;
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_pfp_icebear)
                    .error(R.drawable.ic_default_pfp_icebear)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_default_pfp_icebear);
        }
    }

    @Override
    public int getItemCount() {
        return rankings != null ? rankings.size() : 0;
    }
}
