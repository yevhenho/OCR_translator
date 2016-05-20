package com.example.yevhenho.git_hub_test.network;

import com.example.yevhenho.git_hub_test.models.net.RepoNet;
import com.example.yevhenho.git_hub_test.models.net.UserNet;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;


public interface APIService {

    @GET("/users/{username}")
    Observable<UserNet> getUser(@Path("username") String username);

    @GET("/users/{username}/repos")
    Observable<List<RepoNet>> getUserRepos(@Path("username") String username);
}
