package com.example.yevhenho.websockettest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;


/**
 * Created by yevhenho on 11/23/2015.
 */
public class SendActivity extends BaseActivity {
    public static final int PICK_IMAGE = 1;
    EditText etServer;
    Button btnChange;
    Button btnDefault;
    Button btnChoose;
    Button btnSend;
    ImageView ivPicture;

    private  String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if(resultCode== Activity.RESULT_OK && data != null){
                    setPicture(data);

                }
                break;
        }
    }

    private void setPicture(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);
        cursor.close();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int heightScale = size.x/2;
        int widthScale = size.y;
        Bitmap  bitmap = getScaledBitmap(picturePath, widthScale, heightScale);
        //                            BitmapFactory.decodeFile(picturePath)
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ivPicture.setImageBitmap(bitmap);
        bitmap.recycle();
    }

    @Override
    void setActivityContentView() {
        setContentView(R.layout.activity_send);
    }

    @Override
    void setActivityViews() {
        etServer = (EditText) findViewById(R.id.et_server);
        btnChange = (Button) findViewById(R.id.btn_change);
        btnChange.setOnClickListener(this);
        btnDefault = (Button) findViewById(R.id.btn_default);
        btnDefault.setOnClickListener(this);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        btnChoose.setOnClickListener(this);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change:
                changeServer();
                break;
            case R.id.btn_default:
                changeToDefaultServer();
                break;
            case R.id.btn_choose:
                pickPicture();
                break;
            case R.id.btn_send:
                //todo
              byte [] b = encodeToByteArray(picturePath);

                String s = PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getString(getString(R.string.server_pref_id), getString(R.string.server_default_value));
                break;
        }
    }

    private void changeToDefaultServer() {
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(getString(R.string.server_pref_id), getString(R.string.server_default_value))
                .commit();
    }

    private void changeServer() {
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(getString(R.string.server_pref_id), etServer.getText().toString())
                .commit();
    }

    private void pickPicture() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

}
