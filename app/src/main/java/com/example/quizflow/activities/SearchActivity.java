package com.example.quizflow.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quizflow.R;
import com.example.quizflow.adapters.SearchViewPager2Adapter;
import com.example.quizflow.models.SearchViewModel;
import com.example.quizflow.utils.TYPE;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    private AutoCompleteTextView autoComplete;
    private TabLayout tabL_srchTabs;
    private ViewPager2 vPager2_container;
    private SearchViewModel searchViewModel;

    private String[] tabTitles = {"Quiz", "Category", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // for UI (status bar stuff)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        initViews();
    }

    private void initViews() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> onBackPressed());

        CircleImageView cirImg_addQuiz = findViewById(R.id.cirImg_addQuiz);
        cirImg_addQuiz.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizEditor2Activity.class));
        });

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_topicitem_dropdown, TYPE.topics);
        autoComplete = findViewById(R.id.autoComplete);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1); // show suggestions after 1 character
        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            // handle suggestion click
            String selected = adapter.getItem(position);
            autoComplete.setText(selected, false);
            autoComplete.post(() -> autoComplete.setSelection(autoComplete.getText().length()));
            search(selected);
        });
        autoComplete.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String input = autoComplete.getText().toString().trim();
                search(input);

                // Optional: hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(autoComplete.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        vPager2_container = findViewById(R.id.vPager2_container);
        vPager2_container.setAdapter(new SearchViewPager2Adapter(getSupportFragmentManager(), getLifecycle(), tabTitles.length));

        tabL_srchTabs = findViewById(R.id.tabL_srchTabs);
        for (String title : tabTitles) {
            tabL_srchTabs.addTab(tabL_srchTabs.newTab().setText(title));
        }
        tabL_srchTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vPager2_container.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    cirImg_addQuiz.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    cirImg_addQuiz.setVisibility(View.GONE);
                } else if (tab.getPosition() == 2) {
                    cirImg_addQuiz.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void search(String query) {
        if (searchViewModel != null) {
            searchViewModel.setSearchQuery(query);
        }
    }
}