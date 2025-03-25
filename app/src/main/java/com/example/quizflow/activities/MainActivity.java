package com.example.quizflow.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.quizflow.R;
import com.example.quizflow.fragments.CollectionFragment;
import com.example.quizflow.fragments.HomeFragment;
import com.example.quizflow.fragments.RankingFragment;
import com.example.quizflow.fragments.SettingsFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    private ChipNavigationBar chipNav_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chipNav_menu = findViewById(R.id.chipNav_menu);
        chipNav_menu.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item == R.id.chipNav_homeTab) {
                selectedFragment = new HomeFragment();
            } else if (item == R.id.chipNav_rankingTab) {
                selectedFragment = new RankingFragment();
            } else if (item == R.id.chipNav_collectionTab) {
                selectedFragment = new CollectionFragment();
            } else if (item == R.id.chipNav_settingsTab) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framL_fragContainer, selectedFragment).commit();
            }
        });

        if (savedInstanceState == null) {
            chipNav_menu.setItemSelected(R.id.chipNav_homeTab, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framL_fragContainer, new HomeFragment())
                    .commit();
        }
    }
}