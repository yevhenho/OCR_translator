package org.moocology.ocr;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.language.Language;

import java.io.IOException;
import java.net.URISyntaxException;

public class RecognitionFragment extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String EXTRA_Recognition_URI = "RECintent.Recognition_ID";
    private static String[] languages;
    private static SharedPreferences sharedPreferences;
    Uri recognitionUri;
    Cursor cursor;
    TextView translateResultTextView;
    TextView descTextView;
    TextView errorTextView;
    TextView langTextView;
    EditText resultEditText;
    LinearLayout mLL;
    WebView webView;
    private Spinner sourceSpinner;
    private Spinner targetSpinner;

    public static RecognitionFragment newInstance(Uri uriR) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_Recognition_URI, uriR);
        RecognitionFragment fragment = new RecognitionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Uri getShownIndex() {
        return getArguments().getParcelable(EXTRA_Recognition_URI);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        languages = getResources().getStringArray(R.array.languages_trans);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (resultEditText.getVisibility() == View.VISIBLE) {
            cursor = getActivity().getContentResolver().query(recognitionUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int keyRESULT = cursor.getColumnIndexOrThrow(CONST.RESULT);

                if (!cursor.getString(keyRESULT).equals(resultEditText.getText().toString())) {
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(CONST.RESULT, resultEditText.getText().toString());
                    int updNumber = getActivity().getContentResolver().update(recognitionUri, initialValues, null, null);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recognition, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.menu_item_delete_recognition:
                deleteRecognition();
                return true;
            case R.id.menu_item_send_recognition:
                if (resultEditText.getVisibility() == View.VISIBLE) {
                    shareRecognition();
                } else {
                    Toast.makeText(getActivity(), "No recognition!!!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRecognition() {
        getActivity().getContentResolver().delete(recognitionUri, null, null);
        Intent intent = new Intent(getActivity(), RecognitionListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void shareRecognition() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (translateResultTextView.getVisibility() != View.VISIBLE) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, resultEditText.getText().toString());
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, resultEditText.getText().toString() + System.getProperty("line.separator") + translateResultTextView.getText().toString());
        }

        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_through)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        recognitionUri = getArguments().getParcelable(EXTRA_Recognition_URI);
        cursor = getActivity().getContentResolver().query(recognitionUri, null, null, null, null);
        View view = inflater.inflate(R.layout.recognition_fragment, parent, false);

        int keyDESC = cursor.getColumnIndexOrThrow(CONST.DESC);
        int keyURI = cursor.getColumnIndexOrThrow(CONST.URI);
        int keyDATE = cursor.getColumnIndexOrThrow(CONST.DATE);
        int keyLANG = cursor.getColumnIndexOrThrow(CONST.LANG);
        int keySTATUS = cursor.getColumnIndexOrThrow(CONST.STATUS);
        int keyRESULT = cursor.getColumnIndexOrThrow(CONST.RESULT);
        int keyTRANSLATION = cursor.getColumnIndexOrThrow(CONST.TRANSLATION);

        mLL = (LinearLayout) view.findViewById(R.id.Recognition_fragment_LinearLayout);
        resultEditText = (EditText) view.findViewById(R.id.Recognition_fragment_resultEditText);

        if (cursor.moveToFirst()) {
            Configuration config = getResources().getConfiguration();
            int orientation = config.orientation;
//                SCREEN_ORIENTATION_LAND
            if ((orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) &&
                    (getResources().getConfiguration().smallestScreenWidthDp <= 600)) {
//
                showInWebView(view, cursor, keyURI);

            } else {
//                SCREEN_ORIENTATION_PORTRAIT
//                Show Picture in  webView
                langTextView = (TextView) view.findViewById(R.id.Recognition_fragment_langTextView);
                descTextView = (TextView) view.findViewById(R.id.Recognition_fragment_descTextView);

                descTextView.setText(cursor.getString(keyDESC));
                langTextView.setText(cursor.getString(keyLANG));
                showInWebView(view, cursor, keyURI);
            }

            if (cursor.getString(keyRESULT) != null) {
                final String resultRecognition = cursor.getString(keyRESULT);
//              if picture is recognized
//                    show recognition
                resultEditText.setVisibility(View.VISIBLE);
                resultEditText.setText(cursor.getString(keyRESULT));


                translateResultTextView = (TextView) view.findViewById(R.id.Recognition_fragment_translateResultTextView);
                Button okButton = (Button) view.findViewById(R.id.Recognition_fragment_okButton);


                if (cursor.getString(keyTRANSLATION) != null)

                {
//                              if recognition is translated
                    translateResultTextView.setVisibility(View.VISIBLE);
                    translateResultTextView.setText(cursor.getString(keyTRANSLATION));
                } else

                {
                    initSpinners(view);
                    mLL.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.VISIBLE);
                    okButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (isOnline()) {
                                translate(resultRecognition, sharedPreferences.getString(CONST.KEY_SOURCE_LANGUAGE_PREFERENCE,
                                        CONST.DEFAULT_SOURCE_LANGUAGE), sharedPreferences.getString(CONST.KEY_TARGET_LANGUAGE_PREFERENCE,
                                        CONST.DEFAULT_TARGET_LANGUAGE), translateResultTextView);
                                mLL.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Internet is not available!", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
//
                }
            } else {
//                if picture is not recognized
                mLL.setVisibility(View.GONE);
                resultEditText.setVisibility(View.GONE);
                errorTextView = (TextView) view.findViewById(R.id.Recognition_fragment_errorTextView);
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
        //  initSpinners(view);
        cursor.close();
        return view;
    }

    private boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showInWebView(View view, Cursor cursor, int keyURI) {
        webView = (WebView) view.findViewById(R.id.webview);
        setWebViewParams(webView);
        String pictureUrl = null;
        try {
            pictureUrl = "file://" + getPath(getActivity(), Uri.parse(cursor.getString(keyURI)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        openWebView(pictureUrl);
    }

    public void setWebViewParams(WebView webView) {
         /* WebSettings settings = webView.getSettings();*/
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");

    }

    private void openWebView(String pictureUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        String data = "<div> <img src=\"" + pictureUrl + "\"" + "width=100%" + "/> </div>";
        webView.loadDataWithBaseURL(null, getHtmlData(data), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{display: inline; height: auto; max-width: 100%;}</style> </head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    public void initSpinners(View view) {
        sourceSpinner = (Spinner) view.findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, languages);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);
        sourceSpinner.setOnItemSelectedListener(new SourceLanguageOnItemSelectedListener());

        targetSpinner = (Spinner) view.findViewById(R.id.target_spinner);
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, languages);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetSpinner.setAdapter(targetAdapter);
        targetSpinner.setOnItemSelectedListener(new TargetLanguageOnItemSelectedListener());

        // Set language selections on the spinners based on saved preferences
        String sourceLanguage = sharedPreferences.getString(CONST.KEY_SOURCE_LANGUAGE_PREFERENCE,
                CONST.DEFAULT_SOURCE_LANGUAGE);
        String targetLanguage = sharedPreferences.getString(CONST.KEY_TARGET_LANGUAGE_PREFERENCE,
                CONST.DEFAULT_TARGET_LANGUAGE);
        sourceSpinner.setSelection(sourceAdapter.getPosition(sourceLanguage));
        targetSpinner.setSelection(targetAdapter.getPosition(targetLanguage));
    }


    public class SourceLanguageOnItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String sourceLanguage = (String) sourceSpinner.getItemAtPosition(pos);

            // Save the source language preference
            sharedPreferences.edit().putString(CONST.KEY_SOURCE_LANGUAGE_PREFERENCE,
                    sourceLanguage).commit();


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Do nothing
        }
    }

    public class TargetLanguageOnItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // Save the target language preference
            sharedPreferences.edit().putString(CONST.KEY_TARGET_LANGUAGE_PREFERENCE,
                    (String) targetSpinner.getItemAtPosition(pos)).commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Do nothing
        }
    }

    protected String getPath(Context context, Uri uri)
            throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    String s = cursor.getString(column_index);
                    cursor.close();
                    return s;
                }

            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    void translate(String text, String source, String target, TextView textView) {
        if (source == null || target == null) {
            throw new IllegalArgumentException();
        }

        Language sourceLanguage = null;
        Language targetLanguage = null;
        try {
            sourceLanguage = toLanguage(source);
            targetLanguage = toLanguage(target);
        } catch (IllegalArgumentException e) {
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
            textView.setTextSize(14);
            textView.setText("Unsupported language pair");
        }

        if (sourceLanguage != null && targetLanguage != null) {
            // Start an AsyncTask to perform the translation request.
            ProgressBar progressBar;
            progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
            new TranslateAsyncTask(text, sourceLanguage, targetLanguage,
                    textView, progressBar, getActivity().getContentResolver(), recognitionUri, mLL).execute();
        }

    }


    /**
     * Convert the given name of a natural language into a Language from the enum of Languages
     * supported by this translation service.
     *
     * @param languageName The name of the language, for example, "English"
     * @return The Language object representing this language
     * @throws IllegalArgumentException
     */
    private static Language toLanguage(String languageName) throws IllegalArgumentException {
        // Convert string to all caps
        String standardizedName = languageName.toUpperCase();

        // Replace spaces with underscores
        standardizedName = standardizedName.replace(' ', '_');

        // Remove parentheses
        standardizedName = standardizedName.replace("(", "");
        standardizedName = standardizedName.replace(")", "");

        // Hack to fix misspellings in microsoft-translator-java-api
        if (standardizedName.equals("HAITIAN_CREOLE")) {
            standardizedName = "HATIAN_CREOLE";
        } else if (standardizedName.equals("UKRAINIAN")) {
            standardizedName = "UKRANIAN";
        }

        // Map Norwegian-Bokmal to Norwegian
        if (standardizedName.equals("NORWEGIAN_BOKMAL")) {
            standardizedName = "NORWEGIAN";
        }

        return Language.valueOf(standardizedName);
    }
}


