package com.example.quizflow.respones;

import com.google.gson.annotations.SerializedName;

public class RankingResponse {
    @SerializedName("id")
    private long id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("fullname")
    private String fullname;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("coins")
    private long coins;
    
    // Getters and setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullname() {
        return fullname;
    }
    
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public long getCoins() {
        return coins;
    }
    
    public void setCoins(long coins) {
        this.coins = coins;
    }
} 