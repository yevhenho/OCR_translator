package com.example.yevhenho.git_hub_test.models.view;


public class Repo {
    private int id;
    private int name;
    private String lang;
    private int forks;
    private int stars;

    public Repo(int id, int name, String lang, int forks, int stars) {
        this.id = id;
        this.name = name;
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
