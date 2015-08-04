package org.moocology.ocr;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class RecognitionActivity extends ActionBarActivity {
    Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            // Tablet -multipanel
            finish();
            return;
        }
        setContentView(R.layout.recognition_activity);
        FragmentManager manager = getFragmentManager();
        if (savedInstanceState == null) {
            long id = getIntent().getLongExtra(CONST.Recognition_ID, 0);
            uri = Uri.parse(CONST.CONTENT_URI + "/" + id);
        } else {
            uri = savedInstanceState.getParcelable("uriId");
        }
        Fragment fragment = RecognitionFragment.newInstance(uri);
        manager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("uriId", uri);
    }
}