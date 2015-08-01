package org.moocology.ocr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class RecognitionActivity extends Activity {
    private static final String TAG = "RecognitionActivity";
    Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            Log.d(TAG, "ORIENTATION_LANDSCAPE");
            finish();
            return;
        }


        // During initial setup, plug in the details fragment.
            /*RecognitionFragment details = new RecognitionFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();*/


            setContentView(R.layout.recognition_activity);
            FragmentManager manager = getFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
        if (savedInstanceState == null) {
//            if (fragment == null) {
            long id = getIntent().getLongExtra(CONST.Recognition_ID, 0);

            uri = Uri.parse(CONST.CONTENT_URI + "/" + id);
        }else{
            uri=savedInstanceState.getParcelable("uriId");
        }
            fragment = RecognitionFragment.newInstance(uri);
            manager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("uriId", uri);
    }
}