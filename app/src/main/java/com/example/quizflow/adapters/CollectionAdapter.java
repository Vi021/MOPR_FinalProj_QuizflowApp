package com.example.quizflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.fragments.QuizDetailDialogFragment;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private Context context;
    private List<QuizModel> quizItems;

    public CollectionAdapter(Context context, List<QuizModel> quizItems) {
        this.context = context;
        this.quizItems = quizItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_collectionitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (quizItems == null || quizItems.isEmpty()) {
            return;
        }
        QuizModel item = quizItems.get(position);

        // Bind quiz data
        holder.txtQuizTitle.setText(item.getTitle());
        holder.txtCategory.setText(item.getTopic().toUpperCase());
        holder.txtDuration.setText(item.getDurationString());
        holder.txtQuestionCount.setText(String.valueOf(item.getQuestionCount()));

        // Bind user data
        UserModel user = getUserFromUid(item.getUid());
        if (user != null) {
            holder.txtUser.setText(user.getUsername());
            Glide.with(context)
                    .load(user.getPfp())
                    .placeholder(R.drawable.ic_default_pfp_icebear)
                    .into(holder.cirImgPfp);
        } else {
            holder.txtUser.setText("Unknown");
            holder.cirImgPfp.setImageResource(R.drawable.ic_default_pfp_icebear);
        }

        // Handle item click to show QuizDetailDialogFragment
        holder.itemView.setOnClickListener(v -> {
            QuizDetailDialogFragment dialog = QuizDetailDialogFragment.newInstance(
                    item,
                    user,
                    position
            );
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "quiz_detail");
        });
    }

    @Override
    public int getItemCount() {
        return quizItems != null ? quizItems.size() : 0;
    }

    // Giả lập lấy UserModel từ uid (thay bằng logic thực tế)
    private UserModel getUserFromUid(long uid) {
        // TODO: Thay bằng logic thực tế (API hoặc database, ví dụ: Firebase Firestore)
        // Ví dụ: return new UserModel(uid, "user" + uid, "https://example.com/users/" + uid + "/pfp.jpg");
        return null; // Hiện trả về null để dùng giá trị mặc định
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuizTitle, txtCategory, txtUser, txtDuration, txtQuestionCount;
        CircleImageView cirImgPfp;

        ViewHolder(View itemView) {
            super(itemView);
            txtQuizTitle = itemView.findViewById(R.id.txt_quizTitle);
            txtCategory = itemView.findViewById(R.id.txt_category);
            txtUser = itemView.findViewById(R.id.txt_user);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            txtQuestionCount = itemView.findViewById(R.id.txt_questionCount);
            cirImgPfp = itemView.findViewById(R.id.cirImg_pfp);
        }
    }
}
