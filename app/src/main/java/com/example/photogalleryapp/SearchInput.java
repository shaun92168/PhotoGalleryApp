package com.example.photogalleryapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchInput {
    private Date startTimestamp;
    private Date endTimestamp;
    private String keyword;
    private Double latitude;
    private Double longitude;

    public SearchInput(String from, String to, String keyword, String latitude, String longitude) {
        this.keyword = keyword;

        DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        try {
            this.startTimestamp = format.parse(from);
            this.endTimestamp = format.parse(to);
        } catch (Exception ex) {
            this.startTimestamp = null;
            this.endTimestamp = null;
        }

        if (!latitude.isEmpty())
        {
            this.latitude = Double.valueOf( latitude );
        } else {
            this.latitude = 0.0;
        }
        if (!longitude.isEmpty())
        {
            this.longitude = Double.valueOf( longitude );
        } else {
            this.longitude = 0.0;
        }
    }

    public Date getStartTimestamp() {
        return this.startTimestamp;
    }

    public Date getEndTimestamp() {
        return this.endTimestamp;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }
}
