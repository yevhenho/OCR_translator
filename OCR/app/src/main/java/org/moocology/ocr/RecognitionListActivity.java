package org.moocology.ocr;


import android.app.Activity;
import android.os.Bundle;


public class RecognitionListActivity extends Activity {
    private static final String TAG = "RecognitionListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recognition_listactivity);
    }
}

