package com.example.yevhenho.git_hub_test.util.mapper;

import com.example.yevhenho.git_hub_test.models.net.RepoNet;
import com.example.yevhenho.git_hub_test.models.net.UserNet;
import com.example.yevhenho.git_hub_test.models.view.Repo;
import com.example.yevhenho.git_hub_test.models.view.User;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static User map(UserNet userNet, List<RepoNet> repoNetList) {
        User.Builder builder = new User.Builder();
        List<Repo> repoList = new ArrayList<>();
        for (RepoNet repoNet : repoNetList) {
            repoList.add(map(repoNet));
        }
        builder.name(userNet.getName())
                .login(userNet.getLogin())
                .company(userNet.getCompany())
                .email(userNet.getEmail())
                .avatarUrl(userNet.getAvatarUrl())
                .url(userNet.getUrl())
                .followersCount(userNet.getFollowersCount())
                .followingCount(userNet.getFollowingCount())
                .gistCount(userNet.getGist())
                .repoCount(userNet.getRepos())
                .repos(repoList);
        return builder.build();
    }

    public static Repo map(RepoNet repoNet) {
        return new Repo(repoNet.getId(),
                repoNet.getName(),
                repoNet.getLang(),
                repoNet.getForks(),
                repoNet.getStars());
    }
}
