package com.example.yevhenho.git_hub_test.presenters;

import android.content.Context;

import com.example.yevhenho.git_hub_test.GithubApp;
import com.example.yevhenho.git_hub_test.models.net.RepoNet;
import com.example.yevhenho.git_hub_test.models.net.UserNet;
import com.example.yevhenho.git_hub_test.models.view.User;
import com.example.yevhenho.git_hub_test.network.DataManager;
import com.example.yevhenho.git_hub_test.ui.view.SearchView;
import com.example.yevhenho.git_hub_test.util.mapper.Mapper;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SearchPresenter extends MvpBasePresenter<SearchView> {
    private CompositeSubscription mSubscription;
    private Context mContext;
    @Inject
    BriteDatabase db;

    @Inject
    public SearchPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void attachView(SearchView view) {
        super.attachView(view);
        GithubApp.getComponent(mContext).inject(this);
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            cancelSubscription();
        }
    }

    private void showLoading() {
        if (isViewAttached()) {
            getView().showLoading();
        }
    }

    private void hideLoading() {
        if (isViewAttached()) {
            getView().hideLoading();
        }
    }

    public void showData(User user) {
        if (isViewAttached()) {
            getView().showData(user);
        }
    }

    public void showError(final String msg) {
        if (isViewAttached()) {
            getView().showError(msg);
        }

    }

    private void cancelSubscription() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public void loadData(String name) {
        showLoading();
        Observable<UserNet> userObs = DataManager.getAPIService()
                .getUser(name).subscribeOn(Schedulers.io());
        Observable<List<RepoNet>> reposObs = DataManager.getAPIService()
                .getUserRepos(name).subscribeOn(Schedulers.io());
        Subscription subscription =
                Observable.combineLatest(userObs, reposObs, Mapper::map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {
                                hideLoading();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                showError(e.getMessage());
                            }

                            @Override
                            public void onNext(User user) {
                                hideLoading();
                                showData(user);
                            }
                        });
        mSubscription.add(subscription);
    }

    public void saveUser() {

    }
}
