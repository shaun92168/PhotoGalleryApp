package com.example.photogalleryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
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
        snapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Camera is clicked", Toast.LENGTH_SHORT).show();
                askPermission();
            }

        });



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
                openCamera();
            }
        }
        else{
            // OS < marshmallow
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // Opens Camera Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    // Handling permission result
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // Called when user press allow or deny
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Calling camera app with another intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            image.setImageURI(image_uri);
        }
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SearchActivity.class);

        // Do any initialization of the Search Intent controls here

        startActivity(intent);

    }
}
