package com.example.yevhenho.websockettest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by yevhenho on 11/23/2015.
 */
public class SendActivity extends BaseActivity {
    EditText etServer;
    Button btnChange;
    Button btnDefault;
    Button btnChoose;
    Button btnSend;
    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setActivityContentView() {
        setContentView(R.layout.activity_send);
    }

    @Override
    void setActivityViews() {
        etServer = (EditText) findViewById(R.id.et_server);
        btnChange = (Button) findViewById(R.id.btn_change);
        btnDefault = (Button) findViewById(R.id.btn_default);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        btnSend = (Button) findViewById(R.id.btn_send);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
    }
}
