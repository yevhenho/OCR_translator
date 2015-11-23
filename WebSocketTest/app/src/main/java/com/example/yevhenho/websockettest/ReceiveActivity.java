package com.example.yevhenho.websockettest;

import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by yevhenho on 11/23/2015.
 */
public class ReceiveActivity extends BaseActivity {
    Button btnSave;
    ImageView ivPicture;

    @Override
    void setActivityContentView() {
    setContentView(R.layout.activity_receive);
    }

    @Override
    void setActivityViews() {
        btnSave = (Button) findViewById(R.id.btn_save);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
    }
}
