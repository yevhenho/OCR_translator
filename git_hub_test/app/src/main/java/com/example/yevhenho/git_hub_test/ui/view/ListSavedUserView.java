package com.example.yevhenho.git_hub_test.ui.view;

import com.example.yevhenho.git_hub_test.models.view.User;

import java.util.List;


public interface ListSavedUserView extends BaseView {

    void showData(List<User> users);
}
