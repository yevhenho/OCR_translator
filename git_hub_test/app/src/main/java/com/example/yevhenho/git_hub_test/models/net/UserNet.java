package com.example.yevhenho.git_hub_test.models.net;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yevhenho on 5/20/2016.
 */
public class UserNet {
    private int id;
    private String name;
    private String login;
    private String company;
    private String email;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("html_url")
    private String url;
    @SerializedName("followers")
    private int followersCount;
    @SerializedName("following")
    private int followingCount;
    @SerializedName("public_gists")
    private int gist;
    @SerializedName("public_repos")
    private int repos;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getGist() {
        return gist;
    }

    public int getRepos() {
        return repos;
    }
}
