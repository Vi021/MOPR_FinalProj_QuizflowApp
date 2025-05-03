package com.example.quizflow.utils;

import com.example.quizflow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TYPE {
    public static final Map<String, Integer> CATEGORY = Map.ofEntries(
        Map.entry("GENERAL_KNOWLEDGE", R.drawable.ic_category_general),
        Map.entry("SCIENCE", R.drawable.ic_category_science),
        Map.entry("HISTORY", R.drawable.ic_category_history),
        Map.entry("TECHNOLOGY", R.drawable.ic_category_technology),
        Map.entry("SPORT", R.drawable.ic_category_sport),
        Map.entry("ENTERTAINMENT", R.drawable.ic_category_entertainment),
        Map.entry("GEOGRAPHY", R.drawable.ic_category_geography),
        Map.entry("MATHEMATICS", R.drawable.ic_category_mathematics),
        Map.entry("LITERATURE", R.drawable.ic_category_literature),
        Map.entry("LANGUAGE", R.drawable.ic_category_language),
        Map.entry("PROGRAMMING", R.drawable.ic_category_programming),
        Map.entry("BUSINESS", R.drawable.ic_category_business),
        Map.entry("ART", R.drawable.ic_category_art),
        Map.entry("MUSIC", R.drawable.ic_category_music),
        Map.entry("HEALTH", R.drawable.ic_category_health)
    );
    public static final ArrayList<String> CATEGORIES = new ArrayList<>(CATEGORY.keySet());

    public static final Map<String, Integer> QUESTION = Map.ofEntries(
        Map.entry("MCQ", R.drawable.ic_mcq_white),
        Map.entry("TRUE_FALSE", R.drawable.ic_truefalse_white),
        Map.entry("SHORT_ANSWER", R.drawable.ic_shortanswer_white)
    );
}

