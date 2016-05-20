package com.example.yevhenho.git_hub_test.presenters;

import android.content.Context;

import com.example.yevhenho.git_hub_test.GithubApp;
import com.example.yevhenho.git_hub_test.db.UserDAO;
import com.example.yevhenho.git_hub_test.models.view.User;
import com.example.yevhenho.git_hub_test.ui.view.ListSavedUserView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class ListSavedUserPresenter extends MvpBasePresenter<ListSavedUserView> {
    private CompositeSubscription mSubscription;
    private Context mContext;
    @Inject
    BriteDatabase db;

    @Inject
    public ListSavedUserPresenter(Context context) {
        mContext=context;
    }

    @Override
    public void attachView(ListSavedUserView view) {
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

    public void showData(List<User> users) {
        if (isViewAttached()) {
            getView().showData(users);
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

    public void loadData(){
        Subscription subscription = db.createQuery(UserDAO.TABLE, UserDAO.QUERY)
                .mapToList(UserDAO.MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        mSubscription.add(subscription);
    }




}
