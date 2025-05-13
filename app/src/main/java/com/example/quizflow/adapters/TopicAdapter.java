package com.example.quizflow.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.activities.TopicActivity;
import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.CategoryViewHolder> {
    private final Context context;
    private List<String> categories;
    public void setCategories(List<String> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    private int layoutId = R.layout.row_topicitem;
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public TopicAdapter(Context context, ArrayList<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lineL_category;
        private TextView cateName;
        private ImageView cateIcon;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            lineL_category = itemView.findViewById(R.id.lineL_category);
            cateName = itemView.findViewById(R.id.txt_cateName);
            cateIcon = itemView.findViewById(R.id.img_cateIcon);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutId <= 0) {
            layoutId = R.layout.row_topicitem;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.cateName.setText(Validators.toTitleCase(category));
        holder.cateIcon.setImageResource(Objects.requireNonNull(TYPE.TOPIC.get(category)));
        holder.lineL_category.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicActivity.class);
            intent.putExtra("category", category);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
}
