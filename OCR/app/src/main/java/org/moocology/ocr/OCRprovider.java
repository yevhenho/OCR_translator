package org.moocology.ocr;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class OCRprovider extends ContentProvider {
	private final String LOG_TAG = "OCRprovider";
	private static final int URI_ALLROWS = 1;
	private static final int URI_SINGLE_ROW = 2;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(CONST.AUTHORITY, CONST.RECOGNITION_PATH, URI_ALLROWS);
		uriMatcher.addURI(CONST.AUTHORITY, CONST.RECOGNITION_PATH + "/#", URI_SINGLE_ROW);
	}

	private DBHelper mDBHelper;
	SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "onCreate");
		mDBHelper = new DBHelper(getContext()); 
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Open a read-only database.
		db = mDBHelper.getWritableDatabase();

		// Replace these with valid SQL statements if necessary.
		String groupBy = null;
		String having = null;

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(CONST.DBTableName);

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case URI_SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(CONST._ID + "=" + rowID);
			break;
		case URI_ALLROWS:
			Log.d(LOG_TAG, "URI_ALLROWS");
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);

		}

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, groupBy, having, sortOrder);
		
		cursor.setNotificationUri(getContext().getContentResolver(),
				CONST.CONTENT_URI);
		
	
		return cursor;
		
	}

	@Override
	public String getType(Uri uri) {
		Log.d(LOG_TAG, "getType, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_ALLROWS:
			return CONST.RECOGNITION_CONTENT_TYPE;
		case URI_SINGLE_ROW:
			return CONST.RECOGNITION_CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Open a read / write database to support the transaction.
		if (uriMatcher.match(uri) != URI_ALLROWS)
			throw new IllegalArgumentException("Wrong URI: " + uri);

		db = mDBHelper.getWritableDatabase();
		long rowID = db.insert(CONST.DBTableName, null, values);
		if (rowID > 0) {Uri resultUri = ContentUris.withAppendedId(CONST.CONTENT_URI, rowID);
		// notify ContentResolver, about Change uri resultUri
		getContext().getContentResolver().notifyChange(resultUri, null);
		

		return resultUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	public int getCount(){
		db = mDBHelper.getWritableDatabase();
		long count = db.getPageSize();
		Log.d(LOG_TAG, "Page Size - " + count);
		return (int) count;
		
	}	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(LOG_TAG, "delete, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_ALLROWS:
			Log.d(LOG_TAG, "URI_CONTACTS");
			break;
		case URI_SINGLE_ROW:
			String id = uri.getLastPathSegment();
			Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
			if (TextUtils.isEmpty(selection)) {
				selection = CONST._ID + " = " + id;
			} else {
				selection = selection + " AND " + CONST._ID + " = " + id;
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = mDBHelper.getWritableDatabase();
		int count = db.delete(CONST.DBTableName, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(LOG_TAG, "update, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_ALLROWS:
			Log.d(LOG_TAG, "URI_ALLROWS");

			break;
		case URI_SINGLE_ROW:
			String id = uri.getLastPathSegment();
			Log.d(LOG_TAG, "URI_SINGLE_ROW, " + id);
			if (TextUtils.isEmpty(selection)) {
				selection = CONST._ID + " = " + id;
			} else {
				selection = selection + " AND " + CONST._ID + " = " + id;
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = mDBHelper.getWritableDatabase();
		int count = db.update(CONST.DBTableName, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}



private static class DBHelper extends SQLiteOpenHelper {

	private static final String DBName = "recognitionDB";

	private static final int DBVersion = 1;

	private final String LOG_TAG = "DBHelper";

	

	public DBHelper(Context context) {
		super(context, DBName, null, DBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "--- onCreate database ---");

		db.execSQL("CREATE TABLE if not exists " + CONST.DBTableName + "(" 
				+ CONST._ID	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ CONST.DESC+ " TEXT, " 
				+ CONST.DATE + " TEXT," 
				+ CONST.LANG + " TEXT,"
				+ CONST.TRANSLATION + " TEXT," 
				+ CONST.RESULT + " TEXT,"
				+ CONST.STATUS + " INTEGER," 
				+ CONST.URI + " TEXT NOT NULL"
				+ ");");
		Log.d(LOG_TAG, "database created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "--- onUpgrade database ---");
		db.execSQL("DROP TABLE IF IT EXISTS " + CONST.DBTableName);
		// Create a new one.
		onCreate(db);
	}

}
}