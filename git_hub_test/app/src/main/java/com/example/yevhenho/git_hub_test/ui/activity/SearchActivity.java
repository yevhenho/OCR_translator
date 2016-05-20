package com.example.yevhenho.git_hub_test.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.yevhenho.git_hub_test.R;
import com.example.yevhenho.git_hub_test.models.view.User;
import com.example.yevhenho.git_hub_test.presenters.SearchPresenter;
import com.example.yevhenho.git_hub_test.ui.view.SearchView;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

public class SearchActivity extends MvpViewStateActivity<SearchView, SearchPresenter> implements SearchView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @NonNull
    @Override
    public SearchPresenter createPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    public void showData(User user) {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public ViewState<SearchView> createViewState() {
        return null;
    }

    @Override
    public void onNewViewStateInstance() {

    }
}
