package org.moocology.ocr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;


public class RecognitionActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognition_activity);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            long id = getIntent().getLongExtra(CONST.Recognition_ID, 0);
            Uri uri = Uri.parse(CONST.CONTENT_URI + "/" + id);
            fragment = RecognitionFragment.newInstance(uri);
            manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

}