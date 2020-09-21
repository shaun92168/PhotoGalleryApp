package com.example.photogalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ArrayList<String> photos = null;
    private int index = 0;

    TextView dateTextView;
    EditText latEditText, longiEditText, captionEditText;
    Button leftBtn, rightBtn, snapBtn, uploadBtn;
    Uri image_uri;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = (TextView) findViewById(R.id.TextViewDate);
        latEditText = (EditText) findViewById(R.id.editTextNumberDecimalLatitudeMain);
        longiEditText = (EditText) findViewById(R.id.editTextNumberDecimalLongitudeMain);
        captionEditText = (EditText) findViewById(R.id.editTextCaptionMain);

        leftBtn = (Button) findViewById(R.id.buttonLeftMain);
        rightBtn = (Button) findViewById(R.id.buttonRight);
        snapBtn = (Button) findViewById(R.id.buttonSnapMain);
        uploadBtn = (Button) findViewById(R.id.buttonUploadMain);
        image = (ImageView) findViewById(R.id.imageViewMain);

        photos = findPhotos();
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

        // Ask permission and take photo
        snapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Camera is clicked", Toast.LENGTH_SHORT).show();
                askPermission();
            }
        });

    }

    private ArrayList<String> findPhotos() {
        ArrayList<String> photos = new ArrayList<String>();
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "/Android/data/com.example.photoGallery/files/Pictures");
        //String path = Environment.getExternalStorageDirectory().getPath()+"/Android/data/com.example.photoGallery/files/Pictures";
        File file = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        //Log.d("Files", "Path: " + path);

        File[] fList = file.listFiles();
        Log.d("Files", "file: "+ file);
        Log.d("Files", "Size: "+ fList);

        if (fList != null) {
            for (File f : fList) {
                photos.add(f.getPath());
            }
        }
        return photos;
    }
    private void updatePhoto(String path, String caption) {
        updatePhoto(photos.get(index), (captionEditText).getText().toString());
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
        }
    }
    public void scrollPhotos(View v) {
        switch (v.getId()) {
            case R.id.buttonLeftMain:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.buttonRight:
                if (index < (photos.size() - 1)) {
                index++;
            }
            break;
            default:
                break;
        }
        displayPhoto(photos.get(index));
    }
    private void displayPhoto(String path) {
        if (path == null || path =="") {
            image.setImageResource(R.mipmap.ic_launcher);
            captionEditText.setText("");
            dateTextView.setText("");
        } else {
            image.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            captionEditText.setText(attr[1]);
            dateTextView.setText(attr[2]);
        }
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // Permission not granted, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                // Already granted permission
                dispatchTakePictureIntent();
            }
        }
        else{
            // OS < marshmallow
            dispatchTakePictureIntent();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap original, newBitmap;
                original = (BitmapFactory.decodeFile(currentPhotoPath));
            // Temp fix for the image orientation problem. Camera intent is rotated still.
            image.setImageBitmap(rotateImage(original, 90));
            // Better resolution than Bitmap
//            File f = new File(currentPhotoPath);
//            image.setImageURI(Uri.fromFile(f));
            // Location stored
//            Log.d("tag", "Absolute Url of Image is: " + Uri.fromFile(f));
//
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(f);
//            mediaScanIntent.setData(contentUri);
//            this.sendBroadcast(mediaScanIntent);
        }
    }

    // Save image with timestamp caption
    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Can append geoloction to file name for searching
        String imageFileName = "_caption_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File dir = new File(storageDir.getAbsolutePath() + "/PhotoGallery");
//        dir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        // Saving to gallery instead
        //context.getExternalFilesDirs(null);
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handling permission result
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // Called when user press allow or deny
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    // Calling camera app with another intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SearchActivity.class);

        // Do any initialization of the Search Intent controls here

        startActivity(intent);

    }
}
