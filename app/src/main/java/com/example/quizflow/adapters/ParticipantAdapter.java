package com.example.quizflow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizflow.R;
import com.example.quizflow.models.ParticipantStatus;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    private final List<ParticipantStatus> participants;

    public ParticipantAdapter(List<ParticipantStatus> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParticipantStatus participant = participants.get(position);
        holder.txtUsername.setText(participant.getUsername());
        holder.txtScore.setText("Score: " + participant.getScore());
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtScore;

        ViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_username);
            txtScore = itemView.findViewById(R.id.txt_score);
        }
    }
}
