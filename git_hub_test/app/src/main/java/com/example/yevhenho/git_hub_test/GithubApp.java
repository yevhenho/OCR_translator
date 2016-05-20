package com.example.yevhenho.git_hub_test;

import android.app.Application;
import android.content.Context;

public class GithubApp extends Application {
    private GithubComponent mainComponent;

    @Override public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
        }

        mainComponent = DaggerGithubComponent.builder().githubModule(new GithubModule(this)).build();
    }

    public static GithubComponent getComponent(Context context) {
        return ((GithubApp) context.getApplicationContext()).mainComponent;
    }
}
