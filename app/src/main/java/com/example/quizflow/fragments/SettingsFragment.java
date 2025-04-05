package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizflow.R;

public class SettingsFragment extends Fragment {
    TextView txt_EN, txt_VI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        txt_EN = view.findViewById(R.id.txt_EN);
        txt_VI = view.findViewById(R.id.txt_VI);

        txt_EN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_EN.setBackgroundResource(R.drawable.rounded_bg_white);
                txt_VI.setBackgroundResource(android.R.color.transparent);
            }
        });

        txt_VI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_VI.setBackgroundResource(R.drawable.rounded_bg_white);
                txt_EN.setBackgroundResource(android.R.color.transparent);
                Toast.makeText(getContext(), "I dunno Vietnamese!", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    txt_EN.performClick();
                }, 700);
            }
        });
    }
}