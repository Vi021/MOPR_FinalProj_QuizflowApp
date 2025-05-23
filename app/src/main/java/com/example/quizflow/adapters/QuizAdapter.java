package com.example.quizflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizflow.R;
import com.example.quizflow.fragments.QuizDetailDialogFragment;
import com.example.quizflow.models.AccountModel;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.utils.COLOR;
import com.example.quizflow.utils.Refs;
import com.example.quizflow.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private Context context;
    private List<QuizModel> quizzes;

    private AccountModel user;

    public void setQuizzes(List<QuizModel> quizzes) {
        this.quizzes = quizzes;
        notifyDataSetChanged();
    }

    public QuizAdapter(Context context, List<QuizModel> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout consL_botContainer;
        private ConstraintLayout consL_topContainer;
        private CircleImageView cirImg_pfp;
        private TextView txt_username;
        private TextView txt_topic;
        private TextView txt_title;
        private TextView txt_desc;
        private TextView txt_questionCount;
        private TextView txt_quizDuration;
        private ImageView img_question;
        private ImageView img_timer;
        private ImageView img_singleplayer;
        private ImageView img_availability;

        public QuizViewHolder(View itemView) {
            super(itemView);
            consL_botContainer = itemView.findViewById(R.id.consL_botContainer);
            cirImg_pfp = itemView.findViewById(R.id.cirImg_pfp);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_topic = itemView.findViewById(R.id.txt_topic);

            consL_topContainer = itemView.findViewById(R.id.consL_topContainer);
            txt_title = itemView.findViewById(R.id.txt_quizTitle);
            txt_desc = itemView.findViewById(R.id.txt_quizDesc);
            txt_questionCount = itemView.findViewById(R.id.txt_questionCount);
            txt_quizDuration = itemView.findViewById(R.id.txt_quizDuration);
            img_question = itemView.findViewById(R.id.img_question);
            img_timer = itemView.findViewById(R.id.img_timer);
            img_singleplayer = itemView.findViewById(R.id.img_singleplayer);
            img_availability = itemView.findViewById(R.id.img_availability);
        }
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizViewHolder(LayoutInflater.from(context).inflate(R.layout.row_simplequizitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizModel quiz = quizzes.get(position);

        // user part
        Utilities.getUserByUidAsync(context, quiz.getUid(), new Utilities.AccountCallback() {
            @Override
            public void onSuccess(AccountModel user) {
                QuizAdapter.this.user = user;

                holder.txt_username.setText(user.getUsername());
                String image = user.getImage();
                if (image != null && !image.isEmpty()) {
                    String imageUrl = Refs.BASE_IMAGE_URL + image;
                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_default_pfp_icebear)
                            .error(R.drawable.ic_default_pfp_icebear)
                            .into(holder.cirImg_pfp);
                } else {
                    holder.txt_username.setText("Unknown");
                    holder.cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
                }
            }

            @Override
            public void onFailure(String error) {
                holder.txt_username.setText("Unknown");
                holder.cirImg_pfp.setImageResource(R.drawable.ic_default_pfp_icebear);
            }
        });

        // quiz part
        holder.txt_topic.setText(quiz.getTopic());
        holder.txt_title.setText(quiz.getTitle());
        holder.txt_desc.setText(quiz.getDescription());
        holder.txt_questionCount.setText(String.valueOf(quiz.getQuestionCount()));
        holder.txt_quizDuration.setText(quiz.getDurationString());
        if (quiz.isVisible()) {
            holder.img_availability.setImageResource(R.drawable.ic_globe_white);
        } else {
            holder.img_availability.setImageResource(R.drawable.ic_lock_white);
        }
        holder.consL_topContainer.setOnClickListener(v -> {
            QuizDetailDialogFragment dialog = QuizDetailDialogFragment.newInstance(
                    quiz,
                    user,
                    position
            );
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "quiz_detail");
        });


        // coloring
        Integer color1 = COLOR.QUIZ_ITEM.get(position % COLOR.count);
        Integer color2 = COLOR.QUIZ_ITEM.get(position % COLOR.count + COLOR.count);

        holder.consL_botContainer.setBackgroundTintList(ContextCompat.getColorStateList(context, color1));
        holder.txt_title.setTextColor(ContextCompat.getColorStateList(context, color1));

        holder.txt_desc.setTextColor(ContextCompat.getColorStateList(context, color2));
        holder.txt_questionCount.setTextColor(ContextCompat.getColorStateList(context, color2));
        holder.txt_quizDuration.setTextColor(ContextCompat.getColorStateList(context, color2));
        holder.img_question.setImageTintList(ContextCompat.getColorStateList(context, color2));
        holder.img_timer.setImageTintList(ContextCompat.getColorStateList(context, color2));
        holder.img_singleplayer.setImageTintList(ContextCompat.getColorStateList(context, color2));
        holder.img_availability.setImageTintList(ContextCompat.getColorStateList(context, color2));
    }

    @Override
    public int getItemCount() {
        return quizzes != null ? quizzes.size() : 0;
    }
}
