package com.example.yevhenho.websockettest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvTabSend;
    TextView tvTabReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
    }

    private void setView() {
        tvTabSend = (TextView) findViewById(R.id.tv_tab_send);
        tvTabReceive = (TextView) findViewById(R.id.tv_tab_receive);
    }

    public void onClickTab(View view) {
        switch (view.getId()) {
            case R.id.tv_tab_send:
                startSendActivity();
                break;
            case R.id.tv_tab_receive:
                startReceiveActivity();
                break;
        }
    }
    protected Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);
        sizeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    protected static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    protected static byte[] encodeToByteArray(String file)
    {
        File imagefile = new File(file);
        FileInputStream is = null;
        try {
            is = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(is);

        if (bmp == null) {
            return null;
        }
        else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] data = os.toByteArray();

            return data;
        }
    }

    private void startReceiveActivity() {
    }

    private  void startSendActivity(){

    }

    abstract void setActivityContentView();
    abstract void setActivityViews();
}
