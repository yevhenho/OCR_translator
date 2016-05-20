package com.example.yevhenho.git_hub_test;

import com.example.yevhenho.git_hub_test.presenters.ListSavedUserPresenter;
import com.example.yevhenho.git_hub_test.presenters.SearchPresenter;
import com.example.yevhenho.git_hub_test.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = GithubModule.class)
public interface GithubComponent {
    void inject(MainActivity activity);

    void inject(ListSavedUserPresenter presenter);

    void inject(SearchPresenter presenter);
}
