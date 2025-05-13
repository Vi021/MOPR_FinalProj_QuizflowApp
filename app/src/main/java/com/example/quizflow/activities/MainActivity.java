package com.example.quizflow.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizflow.R;
import com.example.quizflow.fragments.CollectionFragment;
import com.example.quizflow.fragments.HomeFragment;
import com.example.quizflow.fragments.RankingFragment;
import com.example.quizflow.fragments.SettingsFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Objects;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private static final String FTAG_HOME = "HOME";
    private static final String FTAG_RANKING = "RANKING";
    private static final String FTAG_COLLECTION = "COLLECTION";
    private static final String FTAG_SETTINGS = "SETTINGS";

    private ChipNavigationBar chipNav_menu;
    private HomeFragment homeFragment;
    private RankingFragment rankingFragment;
    private CollectionFragment collectionFragment;
    private SettingsFragment settingsFragment;

    private boolean signedIn = false;
    private long backPressedTime = 0;
    private Stack<String> fragmentStack = new Stack<>();

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

        signedIn = getIntent().getBooleanExtra("okay", false);

        // bottom nav bar
        chipNav_menu = findViewById(R.id.chipNav_menu);
        chipNav_menu.setOnItemSelectedListener(item -> {
            if (item == R.id.chipNav_homeTab) {
                displayFragment(FTAG_HOME);
            } else if (item == R.id.chipNav_rankingTab) {
                displayFragment(FTAG_RANKING);
            } else if (item == R.id.chipNav_collectionTab) {
                displayFragment(FTAG_COLLECTION);
            } else if (item == R.id.chipNav_settingsTab) {
                displayFragment(FTAG_SETTINGS);
            }
        });

        // display default fragment
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("FTAG") && getIntent().getStringExtra("FTAG") != null) {
                switchTab(Objects.requireNonNull(getIntent().getStringExtra("FTAG")));
            } else {
                chipNav_menu.setItemSelected(R.id.chipNav_homeTab, true);
                //updateStatusBarTheme();
            }
        }
    }

    // for UI
    private void updateStatusBarTheme() {
        if (!signedIn && chipNav_menu.getSelectedItemId() == R.id.chipNav_homeTab) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
            new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(!((this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES));
        }
    }

    // switch tab based on FTAG (UI)
    private void switchTab(String FTAG) {
        switch (FTAG) {
            case FTAG_RANKING:
                chipNav_menu.setItemSelected(R.id.chipNav_rankingTab, true);
                break;
            case FTAG_COLLECTION:
                chipNav_menu.setItemSelected(R.id.chipNav_collectionTab, true);
                break;
            case FTAG_SETTINGS:
                chipNav_menu.setItemSelected(R.id.chipNav_settingsTab, true);
                break;
            default:
                chipNav_menu.setItemSelected(R.id.chipNav_homeTab, true);
                break;
        }
    }

    // back tracking fragments, max 5 back actions
    private void fragmentBackStacker(String FTAG) {
        if (fragmentStack.isEmpty() || !fragmentStack.peek().equals(FTAG)) {
            if (fragmentStack.size() == 5) {
                fragmentStack.remove(0);    // remove the oldest fragment from the stack
            }
            fragmentStack.push(FTAG);
        }
    }

    private void displayFragment(String FTAG) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    // to switch between fragments

        // init fragments if not already created
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (rankingFragment == null) rankingFragment = new RankingFragment();
        if (collectionFragment == null) collectionFragment = new CollectionFragment();
        if (settingsFragment == null) settingsFragment = new SettingsFragment();

        // hide all fragments
        if (homeFragment.isAdded()) ft.hide(homeFragment);
        if (rankingFragment.isAdded()) ft.hide(rankingFragment);
        if (collectionFragment.isAdded()) ft.hide(collectionFragment);
        if (settingsFragment.isAdded()) ft.hide(settingsFragment);

        // replace fragment based on tag
        Fragment currentFragment;
        switch (FTAG) {
            case FTAG_RANKING:
                currentFragment = rankingFragment;
                break;
            case FTAG_COLLECTION:
                currentFragment = collectionFragment;
                break;
            case FTAG_SETTINGS:
                currentFragment = settingsFragment;
                break;
            default:    // case 0 and others
                currentFragment = homeFragment;
                break;
        }
        if (currentFragment != null) {
            if (!currentFragment.isAdded()) {
                ft.add(R.id.framL_fragContainer, currentFragment, FTAG);
            } else {
                ft.show(currentFragment);
            }

            // track fragments for back navigation
            fragmentBackStacker(FTAG);
        }

        // now do it
        ft.commit();
        updateStatusBarTheme();
    }

    // press back twice to exit app
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < 600) {
            finish();
        } else {
            backPressedTime = System.currentTimeMillis();
            if (!fragmentStack.isEmpty()) {
                fragmentStack.pop();
                if (fragmentStack.isEmpty()) {
                    chipNav_menu.setItemSelected(R.id.chipNav_homeTab, true);
                    Toast toast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
                    toast.show();
                    new android.os.Handler().postDelayed(toast::cancel, 1000);
                } else {
                    String previousFTAG = fragmentStack.peek();
                    switchTab(previousFTAG);
                }
            } else {
                switchTab(FTAG_HOME);
                super.onBackPressed();
            }
        }

    }
}