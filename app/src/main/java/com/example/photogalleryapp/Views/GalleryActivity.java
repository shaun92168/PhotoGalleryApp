package com.example.photogalleryapp.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.photogalleryapp.Presenters.GalleryPresenter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class GalleryActivity extends AppCompatActivity implements GalleryPresenter.View {
    GalleryPresenter presenter;

    private static final int PERMISSION_CODE = 1000;
    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    TextView dateTextView;
    EditText latEditText, longiEditText, captionEditText;
    Button leftBtn, rightBtn, snapBtn, uploadBtn;
    Uri image_uri;
    ImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new GalleryPresenter(this);

        dateTextView = (TextView) findViewById(R.id.etDate);
        latEditText = (EditText) findViewById(R.id.etNumDecLatitudeMain);
        longiEditText = (EditText) findViewById(R.id.etNumDecLongitudeMain);
        captionEditText = (EditText) findViewById(R.id.etCaptionMain);

        leftBtn = (Button) findViewById(R.id.btPrevious);
        rightBtn = (Button) findViewById(R.id.btNext);
        snapBtn = (Button) findViewById(R.id.btSnapMain);
        uploadBtn = (Button) findViewById(R.id.btUploadMain);
        image = (ImageView) findViewById(R.id.imViewMain);

        // Ask permission and take photo
        snapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askPermission();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    upload();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        presenter.StartInitial();
    }


    void upload() throws IOException {
        Drawable mDrawable = image.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }


    public void scrollPhotos(View view) {
        GalleryPresenter.ScrollDirection direction;

        switch (view.getId()){
            case R.id.btPrevious:
            default:
                direction = GalleryPresenter.ScrollDirection.SCROLL_LEFT;
                break;
            case R.id.btNext:
                direction = GalleryPresenter.ScrollDirection.SCROLL_RIGHT;
                break;
        }
        presenter.scrollPhotos1(direction, (captionEditText).getText().toString());
    }


    @Override
    public void displayPhoto(String path) {
        if (path == null || path =="") {
            image.setImageResource(R.mipmap.ic_launcher);
            captionEditText.setText("");
            dateTextView.setText("");
            latEditText.setText("");
            longiEditText.setText("");
        } else {
            image.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            captionEditText.setText(attr[1]);
            dateTextView.setText(attr[2]);
            latEditText.setText(attr[4]);
            longiEditText.setText(attr[5]);
        }
    }


    void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (   checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            {
                // Permission not granted, request it
                String[] permission = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION };
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


    // Handling permission result
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        // Called when user press allow or deny
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(GalleryActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                photoFile = presenter.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                image_uri = FileProvider.getUriForFile(this,
                        "com.example.photogalleryapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.OnActivityResultHandler(requestCode, resultCode, data);
    }


    public void sendMessage(View view) {
        Intent intent = new Intent(this, SearchActivity.class);

        // Do any initialization of the Search Intent controls here
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }
}
