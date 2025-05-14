package com.example.quizflow.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizflow.R;
import com.example.quizflow.activities.AccountActivity;
import com.example.quizflow.activities.SigninActivity;
import com.example.quizflow.utils.Utilities;

public class SettingsFragment extends Fragment {
    ConstraintLayout consL_profileCenter;
    private TextView txt_username, txt_EN, txt_VI, txt_soundOn, txt_soundOff, txt_vibrateOn, txt_vibrateOff, txt_notifyOn, txt_notifyOff, txt_ver;
    private LinearLayout lineL_languageSwitch, lineL_soundSwitch, lineL_vibrateSwitch, lineL_notifySwitch;
    private ImageView img_logout;

    private final boolean[] isEnglish = {true}; // default language is English
    private final boolean[] isSoundOn = {false}; // default sound is OFF
    private final boolean[] isVibrateOn = {false}; // default vibration is OFF
    private final boolean[] isNotifyOn = {false}; // default notification is OFF

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate layout
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        consL_profileCenter = view.findViewById(R.id.consL_profileCenter);
        txt_username = view.findViewById(R.id.txt_username);
        txt_EN = view.findViewById(R.id.txt_EN);
        txt_VI = view.findViewById(R.id.txt_VI);
        txt_soundOn = view.findViewById(R.id.txt_soundOn);
        txt_soundOff = view.findViewById(R.id.txt_soundOff);
        txt_vibrateOn = view.findViewById(R.id.txt_vibrateOn);
        txt_vibrateOff = view.findViewById(R.id.txt_vibrateOff);
        txt_notifyOn = view.findViewById(R.id.txt_notifyOn);
        txt_notifyOff = view.findViewById(R.id.txt_notifyOff);
        txt_ver = view.findViewById(R.id.txt_ver);
        lineL_languageSwitch = view.findViewById(R.id.lineL_languageSwitch);
        lineL_soundSwitch = view.findViewById(R.id.lineL_soundSwitch);
        lineL_vibrateSwitch = view.findViewById(R.id.lineL_vibrateSwitch);
        lineL_notifySwitch = view.findViewById(R.id.lineL_notifySwitch);
        img_logout = view.findViewById(R.id.img_logout);

        // profile center
        consL_profileCenter.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AccountActivity.class));
        });
        txt_username.setText(requireActivity().getIntent().getStringExtra("username")); //temp

        // language
        toggleLanguage();
        lineL_languageSwitch.setOnClickListener(v -> {
            isEnglish[0] = !isEnglish[0]; // toggle language
            toggleLanguage();
        });

        // sound
        toggleSound();
        lineL_soundSwitch.setOnClickListener(v -> {
            isSoundOn[0] = !isSoundOn[0]; // toggle sound
            toggleSound();
        });

        // vibration
        toggleVibration();
        lineL_vibrateSwitch.setOnClickListener(v -> {
            isVibrateOn[0] = !isVibrateOn[0]; // toggle vibration
            toggleVibration();
        });

        // notification
        toggleNotification();
        lineL_notifySwitch.setOnClickListener(v -> {
            isNotifyOn[0] = !isNotifyOn[0]; // toggle notification
            toggleNotification();
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
                Toast toast = Toast.makeText(requireContext(), requiredClicks - clickCount[0] + " more tap(s) to activate Dev Mode", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 700);
            }
        });

        // logout
        img_logout.setOnClickListener(v -> {
            // Clear UID from SharedPreferences
            Utilities.clearUID(requireContext());
            // Show logout confirmation
            Toast toast = Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(toast::cancel, 500);
            // Navigate to SigninActivity and clear activity stack
            Intent intent = new Intent(requireContext(), SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img_logout.setTooltipText("Logout");
        }
    }

    private void toggleLanguage() {
        if (isEnglish[0]) {
            txt_EN.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_VI.setBackgroundResource(android.R.color.transparent);
        } else {
            txt_EN.setBackgroundResource(android.R.color.transparent);
            txt_VI.setBackgroundResource(R.drawable.rounded_bg_white);

            Toast toast = Toast.makeText(getContext(), "I dunno Vietnamese!", Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(() -> {
                lineL_languageSwitch.performClick();
                toast.cancel();
            }, 800);
        }
    }

    private void toggleSound() {
        if (isSoundOn[0]) {
            txt_soundOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_soundOff.setBackgroundResource(android.R.color.transparent);
        } else {
            txt_soundOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_soundOn.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void toggleVibration() {
        if (isVibrateOn[0]) {
            txt_vibrateOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_vibrateOff.setBackgroundResource(android.R.color.transparent);
        } else {
            txt_vibrateOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_vibrateOn.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void toggleNotification() {
        if (isNotifyOn[0]) {
            txt_notifyOn.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_notifyOff.setBackgroundResource(android.R.color.transparent);
        } else {
            txt_notifyOff.setBackgroundResource(R.drawable.rounded_bg_white);
            txt_notifyOn.setBackgroundResource(android.R.color.transparent);
        }
    }
}