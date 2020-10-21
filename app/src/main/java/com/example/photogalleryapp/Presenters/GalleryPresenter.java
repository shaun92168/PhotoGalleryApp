package com.example.photogalleryapp.Presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.photogalleryapp.Models.GPSTracker;
import com.example.photogalleryapp.Models.GalleryModel;
import com.example.photogalleryapp.Views.GalleryActivity;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class GalleryPresenter {
    GalleryActivity mActivityContext;
    GalleryModel model;

    private ArrayList<String> photos = null;
    private int index = 0;
    private String currentPhotoPath;

    public enum ScrollDirection { SCROLL_LEFT, SCROLL_RIGHT};


    // Constructor to receive the Activity context and allow association
    public GalleryPresenter(GalleryActivity context) {
        this.mActivityContext = context;
        model = new GalleryModel(this);
    }


    public Context GetAppContext() {
        return mActivityContext.getApplicationContext();
    }

    public File GetExtFilesDir(String path){
        return mActivityContext.getExternalFilesDir(path);
    }


    public void StartInitial() {
        // Seed the photos list and display the first photos
        index = 0;
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), 0f, 0f,"");
        if (photos.size() == 0) {
            mActivityContext.displayPhoto(null);
        }
        else {
            mActivityContext.displayPhoto(photos.get(index));
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp,
                                         double Latitude, double Longitude, String keywords)
    {
        File file = new File(String.valueOf(mActivityContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                String[] attr = f.getPath().split("_");
                Date timeStamp = new Date();;
                try {
                    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(attr[2] + "_" + attr[3]);
                }
                catch (ParseException e)
                {
                    timeStamp.setTime(0l);
                }


                if (   (   (startTimestamp == null && endTimestamp == null)
                        || (   timeStamp.getTime() >= startTimestamp.getTime()
                            && timeStamp.getTime()/*f.lastModified()*/ <= endTimestamp.getTime()))
                    && (keywords.isEmpty() || attr[1].contains(keywords))
                    && (Latitude == 0.0f || Latitude == Double.valueOf(attr[4]))
                    && (Longitude == 0.0f || Longitude == Double.valueOf(attr[5])) )
                    photos.add(f.getPath());
            }
        }

        return photos;
    }


    private String updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 5) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5] + "_"  + ".jpeg");
            File from = new File(path);
            from.renameTo(to);
            from.renameTo(to);
            photos.set(index, to.getPath());
        }

        // As the name may have changed we pass it back and let the caller use it or not
        return photos.get(index);
    }


    public void scrollPhotos1(ScrollDirection direction, String caption) {
        // Only updatePhoto if there is a photo otherwise an exception would throw
        if (photos.size() > 0) {
            updatePhoto(photos.get(index), caption);
        }

        switch (direction) {
            case SCROLL_LEFT:
                if (index > 0) {
                    index--;
                }
                break;

            case SCROLL_RIGHT:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;

            default:
                break;
        }

        // Can only display photo if there are some otherwise send null
        if (photos.size() > 0) {
            mActivityContext.displayPhoto(photos.get(index));
        }
        else {
            mActivityContext.displayPhoto(null);
        }
    }


    public File createImageFile() throws IOException {
        File imageFile = model.createImageFile();

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }


    public void OnActivityResultHandler(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == mActivityContext.SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == mActivityContext.RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                String slatitude = (String) data.getStringExtra("LAT");
                String slongitude = (String) data.getStringExtra("LNG");
                Double latitude = 0.0;
                Double longitude = 0.0;
                if (slatitude.isEmpty() == false) {
                    latitude = Double.valueOf(slatitude);
                }
                if (slongitude.isEmpty() == false) {
                    longitude = Double.valueOf(slongitude);
                }

                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, latitude, longitude, keywords);
                if (photos.size() == 0) {
                    mActivityContext.displayPhoto(null);
                }
                else {
                    mActivityContext.displayPhoto(photos.get(index));
                }
            }
        }

        if (   requestCode == mActivityContext.REQUEST_IMAGE_CAPTURE && resultCode == mActivityContext.RESULT_OK) {
            photos.add(currentPhotoPath);
            index = photos.size() - 1;
            currentPhotoPath = updatePhoto(currentPhotoPath, "");
            mActivityContext.displayPhoto(currentPhotoPath);
        }
    }


    public interface View {
        public void displayPhoto(String path);
    }
}
