package com.example.yevhenho.git_hub_test.models.net;

import com.google.gson.annotations.SerializedName;

public class RepoNet {
    private int id;
    private int name;
    private UserNet owner;
    @SerializedName("language")
    private String lang;
    @SerializedName("forks_count")
    private int forks;
    @SerializedName("stargazers_count")
    private int stars;

    public RepoNet(int id, int name, UserNet owner, String lang, int forks, int stars) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.lang = lang;
        this.forks = forks;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    public int getName() {
        return name;
    }

    public UserNet getOwner() {
        return owner;
    }

    public String getLang() {
        return lang;
    }

    public int getForks() {
        return forks;
    }

    public int getStars() {
        return stars;
    }
}
