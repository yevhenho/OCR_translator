package org.moocology.ocr;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreSendActivity extends ActionBarActivity {
    final int REQUEST_CODE_GALLERY = 2;
    final int REQUEST_CODE_PHOTO = 1;
    Matrix matrix;
    String mFinishedImageName;
    ImageView mImageView;
    Button reloadButton;
    Button leftRotateButton;
    Button rightRotateButton;
    EditText pre_send_EditText;
    Bitmap rotateBitmap;
    Bitmap bitmap;
    String langString;
    File storageDir;
    String imagePath = null;
    Uri mImageUri = null;
    int res;
    int rotateCount;
    boolean hasResult = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_send);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Intent intent = getIntent();
        res = intent.getIntExtra(ChooserFragment.CODE_INT, ChooserFragment.CODE_FILE);
//spinner
        Spinner spinner = (Spinner) findViewById(R.id.lang_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.select_lang,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. retrieve the selected item
                langString = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        pre_send_EditText = (EditText) findViewById(R.id.pre_send_EditText);
        reloadButton = (Button) findViewById(R.id.reloadButton);
        mImageView = (ImageView) findViewById(R.id.pre_send_ImageView);
        leftRotateButton = (Button) findViewById(R.id.rotateLeftButton);
        rightRotateButton = (Button) findViewById(R.id.rotateRightButton);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (imagePath == null) {
            if (!hasResult) {
                if (res == ChooserFragment.CODE_CAMERA) {
                    dispatchintent();
                } else
                //CODE_FILE)
                {
                    choosePhoto();
                }
                hasResult = true;
            } else {
                reloadButton.setVisibility(View.VISIBLE);
                mImageView.setImageResource(R.drawable.no_image);
                rightRotateButton.setVisibility(View.GONE);
                leftRotateButton.setVisibility(View.GONE);
            }
        } else {
            setPic(imagePath, mImageView, rotateCount);
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("hasResult", hasResult);
        savedInstanceState.putString("imagePath", imagePath);
        savedInstanceState.putInt("rotateCount", rotateCount);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hasResult = savedInstanceState.getBoolean("hasResult");
        imagePath = savedInstanceState.getString("imagePath");
        rotateCount = savedInstanceState.getInt("rotateCount");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_GALLERY:
                    mImageUri = data.getData();
                    if (mImageUri != null) {
                        try {
                            imagePath = getPath(getBaseContext(), mImageUri);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                case REQUEST_CODE_PHOTO:
                    if (imagePath != null) {
                        mImageUri = getImageContentUri(getBaseContext(), new File(
                                imagePath));
                        galleryAddPic(imagePath);
                    }
            }
        } else {
            imagePath = null;
            Toast.makeText(getBaseContext(),
                    "Choosing a photo have cancelled. Try again!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send: {
                MyApplication.tracker().send(new HitBuilders.EventBuilder("ui", "send")
                        .setLabel("recognition")
                        .build());
                String desc = pre_send_EditText.getText().toString();
                if (imagePath == null || desc.equals("")) {
                    Toast.makeText(this, "You do not choose photo or enter desc", Toast.LENGTH_LONG).show();
                } else {
                    String date = formatDate();

                    if (rotateCount % 4 != 0) {
                        saveBitmapToDisk(rotateBitmap);
                        imagePath = storageDir.toString() + "/" + mFinishedImageName;
                        mImageUri = getImageContentUri(getBaseContext(), new File(imagePath));
                        galleryAddPic(imagePath);
                    }

//              ContentProvider: add new rec
                    Uri newUri = addNewRecToCP(desc, date, langString, mImageUri);
                    //start service
                    startIntent(newUri, langString);
                    //start new intent to 	RecognitionListActivity.class
                    Intent intent = new Intent(this, RecognitionListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //
                    startActivity(intent);
                }
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveBitmapToDisk(Bitmap bitmap) {
        mFinishedImageName = imagePath.substring(storageDir.toString().length() + 1,
                imagePath.length() - 4) + "_rotated.jpg";
        File outFile = new File(storageDir, mFinishedImageName);
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    private String formatDate() {
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yy HH:mm",
                Locale.getDefault());
        return formater.format(new Date());
    }

    private Uri addNewRecToCP(String desc, String date, String lang, Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues initialValues = new ContentValues();
        initialValues.put(CONST.DESC, desc);
        initialValues.put(CONST.DATE, date);
        initialValues.put(CONST.LANG, lang);
        initialValues.put(CONST.URI, uri.toString());
        return contentResolver.insert(CONST.CONTENT_URI, initialValues);
    }

    private void startIntent(Uri newUri, String lang) {
        Intent servicdIntent = new Intent(PreSendActivity.this, UploadService.class);
        servicdIntent.putExtra(CONST.KEY_IMAGE_URL, mImageUri.toString());
        servicdIntent.putExtra(CONST.Recognition_URI, newUri);
        servicdIntent.putExtra("lang", lang);
        servicdIntent.putExtra("psm", 3);
        startService(servicdIntent);

    }

    public void choosePhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //it was File.createTempFile
        File image = new File(storageDir, imageFileName + ".jpg" /* prefix */
             /* suffix */
                 /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents

        imagePath = image.getAbsolutePath();
        //"file:"+


        return image;
    }

    private void dispatchintent() {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {

                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(intent, REQUEST_CODE_PHOTO);

            }
        }
    }


    protected void setPic(String picPath, ImageView imageView, int rotateNumber) {
        // Get the dimensions of the bitmap
        final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565; // 2x reduce size
        bitmap = BitmapFactory.decodeFile(picPath);
        setScaleType(rotateNumber);
        bmOptions.inJustDecodeBounds = false;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthScale = size.y;
        bmOptions.inSampleSize = Math.round((bitmap.getWidth() + bitmap.getHeight()) / 2 / widthScale);
        // scaleFactor;
        if (rotateNumber % 4 == 0) {
            rotateBitmap = BitmapFactory.decodeFile(picPath, bmOptions);
        } else {
            bitmap = BitmapFactory.decodeFile(picPath, bmOptions);
            rotateBitmap = rotationBitmap(bitmap, rotateNumber);
        }
/*        Log.d(TAG, String.format("bitmap size = %sx%s, byteCount = %s",
                rotateBitmap.getWidth(), rotateBitmap.getHeight(), rotateBitmap.getByteCount()));*/
        imageView.setImageBitmap(rotateBitmap);

    }

    private void setScaleType(int rotateNumber) {
        Configuration conf = getResources().getConfiguration();
        int orientation = conf.orientation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (Math.pow(-1, rotateNumber) * bitmap.getHeight() >= Math.pow(-1, rotateNumber) * bitmap.getWidth()) {
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                mImageView.setScaleType(ImageView.ScaleType.FIT_START);
            }
        } else {
            if (Math.pow(-1, rotateNumber) * bitmap.getHeight() >= Math.pow(-1, rotateNumber) * bitmap.getWidth()) {
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            }
        }
    }

    private Bitmap rotationBitmap(Bitmap bitmap, int rotateNumber) {
        matrix = new Matrix();
        matrix.postRotate(rotateNumber * 90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {

            int _id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + _id);

        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }

    }

    public void startChooser() {
        FragmentManager fm = this.getFragmentManager();
        ChooserFragment dialog = ChooserFragment.newInstance();
        dialog.show(fm, "ss");
    }

    public void reloadPhoto(View v) {
        startChooser();
    }

    public void rotateLeftPhoto(View v) {
        --rotateCount;
        setPic(imagePath, mImageView, rotateCount);

    }

    public void rotateRightPhoto(View v) {
        ++rotateCount;
        setPic(imagePath, mImageView, rotateCount);

    }

}
