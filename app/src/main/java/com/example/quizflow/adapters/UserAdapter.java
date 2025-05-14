package com.example.quizflow.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.activities.UserDetailActivity;
import com.example.quizflow.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;

    private List<UserModel> users;

    public void setUsers(List<UserModel> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public UserAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lineL_user;
        private TextView txt_username;
        private CircleImageView cirImg_pfp;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            lineL_user = itemView.findViewById(R.id.lineL_user);
            txt_username = itemView.findViewById(R.id.txt_username);
            cirImg_pfp = itemView.findViewById(R.id.cirImg_pfp);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.row_useritem_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = users.get(position);

        holder.txt_username.setText(user.getUsername());

        if (user.getPfp() == null || user.getPfp().isEmpty()) {
            holder.cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
        } else {
            Glide.with(context).load(user.getPfp()).into(holder.cirImg_pfp);
        }

        holder.lineL_user.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("USER_DETAIL_ID", user.getId());        // TODO: link
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }
}
