package com.example.yevhenho.git_hub_test.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.yevhenho.git_hub_test.R;
import com.example.yevhenho.git_hub_test.models.view.User;
import com.example.yevhenho.git_hub_test.presenters.ListSavedUserPresenter;
import com.example.yevhenho.git_hub_test.ui.view.ListSavedUserView;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

import java.util.List;

public class ListSavedUserActivity extends MvpViewStateActivity<ListSavedUserView, ListSavedUserPresenter> implements ListSavedUserView {
    private ProgressDialog mProgressDialog;

    @NonNull
    @Override
    public ListSavedUserPresenter createPresenter() {
        return new ListSavedUserPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_saved_user);
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(ListSavedUserActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Title");
        mProgressDialog.setMessage("Message");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void showData(List<User> users) {

    }

    @Override
    public ViewState<ListSavedUserView> createViewState() {
        return new ViewState<ListSavedUserView>() {
            @Override
            public void apply(ListSavedUserView view, boolean retained) {

            }
        };
    }

    @Override
    public void onNewViewStateInstance() {

    }
}
