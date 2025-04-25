package com.example.quizflow;

import com.example.quizflow.utils.Refs;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2Client {
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Refs.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private APIService api = retrofit.create(APIService.class);

    public APIService getAPI() {
        return api;
    }
}
