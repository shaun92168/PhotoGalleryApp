package com.example.photogalleryapp.Models;

import android.os.Environment;
import android.widget.Toast;

import com.example.photogalleryapp.Presenters.GalleryPresenter;
import com.example.photogalleryapp.Views.GalleryActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GalleryModel {
    GalleryPresenter mPresenterContext;


    // Constructor to receive the Activity context and allow association
    public GalleryModel(GalleryPresenter context) {
        this.mPresenterContext = context;
    }

    // Save image with timestamp caption
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Get GPS data
        double latitude = 0f;
        double longitude = 0f;
        GPSTracker gps = new GPSTracker(mPresenterContext.GetAppContext());

        if (gps.canGetLocation())
        {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(mPresenterContext.GetAppContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
        }

        // Can append geoloction to file name for searching
        String imageFileName = "__" + timeStamp + "_" + latitude + "_" + longitude + "_";

        File storageDir = mPresenterContext.GetExtFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpeg", storageDir);

        return imageFile;
    }
}
