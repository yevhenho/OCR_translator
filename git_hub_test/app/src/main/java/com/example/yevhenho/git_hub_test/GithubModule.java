package com.example.yevhenho.git_hub_test;


import android.app.Application;

import com.example.yevhenho.git_hub_test.db.DbModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DbModule.class,
        }
)
public final class GithubModule {
    private final Application application;

    GithubModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}