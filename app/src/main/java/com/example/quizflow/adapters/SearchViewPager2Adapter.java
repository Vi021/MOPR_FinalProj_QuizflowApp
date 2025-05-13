package com.example.quizflow.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quizflow.fragments.QuizVP2Fragment;
import com.example.quizflow.fragments.TopicVP2Fragment;
import com.example.quizflow.fragments.UserVP2Fragment;

public class SearchViewPager2Adapter extends FragmentStateAdapter {
    private int numOfTabs;

    private QuizVP2Fragment quizVP2Fragment;
    private TopicVP2Fragment topicVP2Fragment;
    private UserVP2Fragment userVP2Fragment;

    public SearchViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numOfTabs) {
        super(fragmentManager, lifecycle);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (quizVP2Fragment == null) quizVP2Fragment = new QuizVP2Fragment();
        if (topicVP2Fragment == null) topicVP2Fragment = new TopicVP2Fragment();
        if (userVP2Fragment == null) userVP2Fragment = new UserVP2Fragment();

        switch (position) {
            case 0:
                return quizVP2Fragment;
            case 1:
                return topicVP2Fragment;
            case 2:
                return userVP2Fragment;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
