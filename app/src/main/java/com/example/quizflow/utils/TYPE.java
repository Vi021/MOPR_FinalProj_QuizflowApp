package com.example.quizflow.utils;

import com.example.quizflow.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class TYPE {
    public static final Map<String, Integer> TOPIC = Map.ofEntries(
        Map.entry("ART", R.drawable.ic_category_art),
        Map.entry("BUSINESS", R.drawable.ic_category_business),
        Map.entry("ENTERTAINMENT", R.drawable.ic_category_entertainment),
        Map.entry("GENERAL_KNOWLEDGE", R.drawable.ic_category_general),
        Map.entry("GEOGRAPHY", R.drawable.ic_category_geography),
        Map.entry("HEALTH", R.drawable.ic_category_health),
        Map.entry("HISTORY", R.drawable.ic_category_history),
        Map.entry("LANGUAGE", R.drawable.ic_category_language),
        Map.entry("LITERATURE", R.drawable.ic_category_literature),
        Map.entry("MATHEMATICS", R.drawable.ic_category_mathematics),
        Map.entry("MUSIC", R.drawable.ic_category_music),
        Map.entry("PROGRAMMING", R.drawable.ic_category_programming),
        Map.entry("SCIENCE", R.drawable.ic_category_science),
        Map.entry("SPORT", R.drawable.ic_category_sport),
        Map.entry("TECHNOLOGY", R.drawable.ic_category_technology)
    );
    public static final ArrayList<String> TOPICS = new ArrayList<>(TOPIC.keySet());
    static {
        Collections.sort(TOPICS);
    }
    public static final ArrayList<String> Topics = TOPICS.stream()
            .map(Utilities::toTitleCase)
            .collect(Collectors.toCollection(ArrayList::new));
    public static final ArrayList<String> topics = TOPICS.stream()
            .map(Utilities::toLowerCase)
            .collect(Collectors.toCollection(ArrayList::new));

    public static final Map<String, Integer> QUESTION = Map.ofEntries(
        Map.entry("MCQ", R.drawable.ic_mcq_white),          // Multiple Choice Question
        Map.entry("TF", R.drawable.ic_truefalse_white),     // True/False
        Map.entry("SA", R.drawable.ic_shortanswer_white)    // Short Answer
    );
    public static final ArrayList<String> QUESTIONS = new ArrayList<>(QUESTION.keySet());
    static {
        Collections.sort(QUESTIONS);
    }
}

