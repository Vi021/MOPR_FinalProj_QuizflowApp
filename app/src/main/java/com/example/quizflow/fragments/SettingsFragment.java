package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizflow.R;

public class SettingsFragment extends Fragment {
    TextView txt_EN, txt_VI, txt_soundOn, txt_soundOff, txt_vibrateOn, txt_vibrateOff, txt_notifyOn, txt_notifyOff, txt_ver;
    ImageView img_logout;

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
        txt_soundOn = view.findViewById(R.id.txt_soundOn);
        txt_soundOff = view.findViewById(R.id.txt_soundOff);
        txt_vibrateOn = view.findViewById(R.id.txt_vibrateOn);
        txt_vibrateOff = view.findViewById(R.id.txt_vibrateOff);
        txt_notifyOn = view.findViewById(R.id.txt_notifyOn);
        txt_notifyOff = view.findViewById(R.id.txt_notifyOff);
        txt_ver = view.findViewById(R.id.txt_ver);
        img_logout = view.findViewById(R.id.img_logout);

        // language
        txt_EN.setOnClickListener(v -> {
            txt_EN.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_VI.setBackgroundResource(android.R.color.transparent);
        });
        txt_VI.setOnClickListener(v -> {
            txt_VI.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_EN.setBackgroundResource(android.R.color.transparent);
            Toast.makeText(getContext(), "I dunno Vietnamese!", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                txt_EN.performClick();
            }, 700);
        });

        // sound
        txt_soundOn.setOnClickListener(v -> {
            txt_soundOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_soundOff.setBackgroundResource(android.R.color.transparent);
        });
        txt_soundOff.setOnClickListener(v -> {
            txt_soundOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_soundOn.setBackgroundResource(android.R.color.transparent);
        });

        // vibration
        txt_vibrateOn.setOnClickListener(v -> {
            txt_vibrateOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_vibrateOff.setBackgroundResource(android.R.color.transparent);
        });
        txt_vibrateOff.setOnClickListener(v -> {
            txt_vibrateOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_vibrateOn.setBackgroundResource(android.R.color.transparent);
        });

        // notification
        txt_notifyOn.setOnClickListener(v -> {
            txt_notifyOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_notifyOff.setBackgroundResource(android.R.color.transparent);
        });
        txt_notifyOff.setOnClickListener(v -> {
            txt_notifyOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_notifyOn.setBackgroundResource(android.R.color.transparent);
        });

        // about
        final int[] clickCount = {0};
        final int requiredClicks = 7;
        Handler aboutHandler = new Handler();
        Runnable resetTapCounter = () -> clickCount[0] = 0;
        txt_ver.setOnClickListener(v -> {
            clickCount[0]++;
            aboutHandler.removeCallbacks(resetTapCounter); // cancel previous timer
            aboutHandler.postDelayed(resetTapCounter, 2000); // reset if 2 sec gap

            if (clickCount[0] >= requiredClicks) {
                Toast toast = Toast.makeText(requireContext(), "Hah! In ya dream!", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 600);
            } else {
                Toast toast = Toast.makeText(requireContext(), requiredClicks - clickCount[0] + " more taps to activate Dev Mode", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 700);
            }
        });

        // logout
        img_logout.setOnClickListener(v -> {
            // TODO:
            //  show "u sure?" dialog, maybe?
            //  logout and return to login screen
            Toast toast = Toast.makeText(requireContext(), "Nuh uh!", Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(toast::cancel, 500);
        });
    }
}