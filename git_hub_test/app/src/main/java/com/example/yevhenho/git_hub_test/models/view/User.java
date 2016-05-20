package com.example.yevhenho.git_hub_test.models.view;


import java.util.List;

public class User {
    private String name;
    private String login;
    private String company;
    private String email;
    private String avatarUrl;
    private String url;
    private int followersCount;
    private int followingCount;
    private int gistCount;
    private int repoCount;
    private List<Repo> repos;

    private User(Builder builder) {
        this.name = builder.name;
        this.login = builder.login;
        this.company = builder.company;
        this.email = builder.email;
        this.avatarUrl = builder.avatarUrl;
        this.url = builder.url;
        this.followersCount = builder.followersCount;
        this.followingCount = builder.followingCount;
        this.gistCount = builder.gistCount;
        this.repoCount = builder.repoCount;
        this.repos = builder.repos;
    }

    public static class Builder {
        private String name;
        private String login;
        private String company;
        private String email;
        private String avatarUrl;
        private String url;
        private int followersCount;
        private int followingCount;
        private int gistCount;
        private int repoCount;
        private List<Repo> repos;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder followersCount(int followersCount) {
            this.followersCount = followersCount;
            return this;
        }

        public Builder followingCount(int followingCount) {
            this.followingCount = followingCount;
            return this;
        }

        public Builder gistCount(int gistCount) {
            this.gistCount = gistCount;
            return this;
        }

        public Builder repoCount(int repoCount) {
            this.repoCount = repoCount;
            return this;
        }

        public Builder repos(List<Repo> repos) {
            this.repos = repos;
            return this;
        }

        public User build() {
            return new User(this);
        }

        public Builder() {
        }
    }
}
