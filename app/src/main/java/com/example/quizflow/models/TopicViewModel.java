package com.example.quizflow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizflow.utils.TYPE;
import com.example.quizflow.utils.Validators;

import java.util.List;
import java.util.stream.Collectors;

public class TopicViewModel extends ViewModel {
    private final MutableLiveData<List<String>> topics = new MutableLiveData<>();

    public LiveData<List<String>> getTopics() {
        return topics;
    }

    public void fetchTopics(String query) {
        topics.postValue(
                TYPE.TOPICS.stream()
                        .filter(topic -> Validators.toLowerCase(topic).contains(Validators.toLowerCase(query)))
                        .collect(Collectors.toList())
        );
    }
}
