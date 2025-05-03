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

import java.util.ArrayList;
import java.util.Objects;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.CategoryViewHolder> {
    private final Context context;
    private final ArrayList<String> categories;

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
        View view = LayoutInflater.from(context).inflate(R.layout.row_categoryitem, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.cateName.setText(category);
        holder.cateIcon.setImageResource(Objects.requireNonNull(TYPE.TOPIC.get(category)));
        holder.lineL_category.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicActivity.class);
            intent.putExtra("category", category);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
}
