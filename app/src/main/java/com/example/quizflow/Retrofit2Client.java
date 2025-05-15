package com.example.quizflow;

import com.example.quizflow.utils.Refs;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2Client {
    private static Retrofit retrofit = null;
    
    public static synchronized Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(Refs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
    
    private static APIService api = null;
    
    public static APIService getAPI() {
        if (api == null) {
            api = getRetrofitInstance().create(APIService.class);
        }
        return api;
    }
}
