package org.moocology.ocr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UploadService extends IntentService {

	private int mId ;
	private static final String TAG = "UploadService";

	
	public UploadService() {
		super("UploadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Uri uriRecognition =  intent.getParcelableExtra(CONST.Recognition_URI);
		String urlImage = intent.getStringExtra(CONST.KEY_IMAGE_URL);
			
		Uri uriImage = Uri.parse(urlImage);
		String path = getRealPathFromURI(this, uriImage);
		final File image = new File(path);
		String lang = "eng";
		String psm = "3";
		if (image.exists()) {
			String data = uploadUserPhoto(image);

			String text = getRequest(data, lang, psm);
			ContentResolver contentResolver = getContentResolver();
			ContentValues initialValues = new ContentValues();
			initialValues.put(CONST.RESULT, text);
			if(text==null){
				initialValues.put(CONST.STATUS, CONST.STATUS_NO_RECOGNIZED);
			} else{
			initialValues.put(CONST.STATUS, CONST.STATUS_RECOGNIZED);
			}
			int updNumber = contentResolver.update(uriRecognition, initialValues, null, null);
						// SEND Notification
			sendNotification(uriRecognition);
		}
	}

	private void sendNotification(Uri uriRecognition) {
		 Resources r = getResources();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setTicker(r.getString(R.string.new_recognition_deliver_1))
				.setSmallIcon(android.R.drawable.ic_menu_report_image)
				.setContentTitle(
						r.getString(R.string.new_recognition_deliver_2))
				.setContentText(
						r.getString(R.string.new_recognition_deliver_3))
				// delete from panel after touch
				.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent;
		if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
			resultIntent = new Intent(this, RecognitionListActivity.class);

		}else{
		resultIntent = new Intent(this, RecognitionActivity.class);
		resultIntent
				.putExtra(CONST.Recognition_ID, Long.valueOf(uriRecognition.getLastPathSegment()));}

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(RecognitionActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		mId++;
	}

	public static class HttpClientFactory {
		private static DefaultHttpClient client;
		public synchronized static DefaultHttpClient getThreadSafeClient() {
			if (client != null)
				return client;
			client = new DefaultHttpClient();
			ClientConnectionManager mgr = client.getConnectionManager();
			HttpParams params = client.getParams();
			client = new DefaultHttpClient(new ThreadSafeClientConnManager(
					params, mgr.getSchemeRegistry()), params);
			return client;
		}
	}

	public String uploadUserPhoto(File image) {
		try {
			HttpClient client = HttpClientFactory.getThreadSafeClient();
			HttpPost post = new HttpPost(CONST.Uri_UPLOAD);
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (image != null) {
				entityBuilder.addBinaryBody("file", image);
			}
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			return EntityUtils.toString(httpEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public String getRequest(String json, String lang, String psm) {
		String text = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject data = jsonObject.getJSONObject("data");
			String file_id =  data.getString("file_id");
			String pages =  data.getString("pages");
StringBuilder stringBuilder= new StringBuilder(CONST.Uri_OCR);
			stringBuilder.append("&file_id=");
			stringBuilder.append(file_id);
			stringBuilder.append("&page=");
			stringBuilder.append(pages);
			stringBuilder.append("&lang=");
			stringBuilder.append(lang);
			stringBuilder.append("&psm=");
			stringBuilder.append(psm);

			String link = stringBuilder.toString();

			DefaultHttpClient hc = new DefaultHttpClient();
			ResponseHandler<String> res = new BasicResponseHandler();
			HttpGet http = new HttpGet(link);
			String json2;
			try {
				json2 = hc.execute(http, res);
				JSONObject json3 = new JSONObject(json2);
				JSONObject datas = json3.getJSONObject("data");
				text = datas.getString("text");
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return text;
	}
}
