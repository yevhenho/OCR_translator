package com.example.yevhenho.git_hub_test.ui.view;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface BaseView extends MvpView {
    void showError(final String msg);

    void showLoading();

    void hideLoading();
}
