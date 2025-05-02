package com.example.quizflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.utils.TYPE;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final Context context;
    private final ArrayList<String> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(Context context, ArrayList<String> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
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
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView cateName;
        private final ImageView cateIcon;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            cateName = itemView.findViewById(R.id.txt_cateName);
            cateIcon = itemView.findViewById(R.id.img_cateIcon);
        }

        public void bind(String category, OnCategoryClickListener listener) {
            cateName.setText(category);
            cateIcon.setImageResource(Objects.requireNonNullElseGet(TYPE.CATEGORY.get(category), () -> R.drawable.ic_help_white));
            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
}
