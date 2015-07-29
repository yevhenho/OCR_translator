package org.moocology.ocr;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CONST implements BaseColumns {
    // Default languages
    public static final String DEFAULT_SOURCE_LANGUAGE = "English";//
    public static final String DEFAULT_TARGET_LANGUAGE = "Spanish";//

    // Language settings
    public static final String KEY_SOURCE_LANGUAGE_PREFERENCE = "preference_source_language";//
    public static final String KEY_TARGET_LANGUAGE_PREFERENCE = "preference_target_language";//

    //String
    public static final String BROADCAST_ACTION = "org.moocology.ocr.BROADCAST";
    public static final String PERM_PRIVATE = "org.moocology.ocr.PRIVATE";

    public static final String MICROSOFT_TRANSLATE_ID = "yev2301sem";
    public static final String MICROSOFT_TRANSLATE_SECRET = "NiwLlTWuiULtchuPnrDGFS5DZM/GADQsz9V0aCsZqi8=";
    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS = "org.moocology.ocr.STATUS";
    public static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";
    public static final String Recognition_ID = "Recognition_ID";
    public static final String Recognition_URI = "Recognition_URI";


    //int
    public static final int NO_ROTATE = 0;
    public static final int LEFT_ROTATE = 1;
    public static final int RIGHT_ROTATE = 2;
    public static final int STATUS_RECOGNIZED = 10;
    public static final int STATUS_NO_RECOGNIZED = 11;
    public static final int DRAWABLE_YES = R.drawable.btn_check_on_focused_holo_light;
    public static final int DRAWABLE_NO = R.drawable.btn_check_off_focused_holo_light;
    //CP

    public static final String DBTableName = "recognitionTable";
    public static final Uri CONTENT_URI = Uri.parse("content://org.moocology.OCRprovider/recognitions");
    public static final String AUTHORITY = "org.moocology.OCRprovider";
    public static final String RECOGNITION_PATH = "recognitions";


    public static final String RECOGNITION_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + RECOGNITION_PATH;
    public static final String RECOGNITION_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + RECOGNITION_PATH;


    public final static String[] columns = {CONST._ID, CONST.DESC, CONST.DATE, CONST.LANG,
            CONST.TRANSLATION, CONST.RESULT, CONST.URI};
    public static final String DESC = "desc";
    public static final String DATE = "date";
    public static final String LANG = "lang";
    public static final String TRANSLATION = "translation";
    public static final String RESULT = "result";
    public static final String STATUS = "status";
    public static final String URI = "uri";


}
