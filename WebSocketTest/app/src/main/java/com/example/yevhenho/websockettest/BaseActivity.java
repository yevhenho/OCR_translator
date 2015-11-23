package com.example.yevhenho.websockettest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {
TextView tvTabSend;
TextView tvTabReceive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
    }
     private void setView(){
         tvTabSend= (TextView)findViewById(R.id.tv_tab_send);
         tvTabReceive= (TextView)findViewById(R.id.tv_tab_receive);
     }
    abstract void setActivityContentView();
    abstract void setActivityViews();
}
