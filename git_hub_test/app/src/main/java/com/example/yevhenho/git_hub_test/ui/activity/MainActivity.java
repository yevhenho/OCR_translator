package com.example.yevhenho.git_hub_test.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.yevhenho.git_hub_test.GithubApp;
import com.example.yevhenho.git_hub_test.R;
import com.example.yevhenho.git_hub_test.util.Const;
import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Inject
    BriteDatabase db;

    @OnClick(R.id.btn_saved)
    protected void onSavedClick() {
        navigateToSavedUserAc();
    }

    @OnClick(R.id.btn_search)
    protected void onSearchClick() {
        navigateToSearchAc();
    }

    @BindView(R.id.et_name)
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoTitle();
        setFullScreen();
        GithubApp.getComponent(this).inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);
//        setTitle(toolbar);
//        ((TextView)toolbar.findViewById(R.id.tv_title)).setText(getString(R.string.app_name));
    }

    private void setNoTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setTitle(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.app_name));
    }
    private void navigateToSearchAc() {
        Intent intent = new Intent(this, SearchActivity.class);
        String name = etName.getText().toString();
        intent.putExtra(Const.ARG.NAME, name);
        startActivity(intent);
    }
    private void navigateToSavedUserAc() {
        Intent intent = new Intent(this, ListSavedUserActivity.class);
        startActivity(intent);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
