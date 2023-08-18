package com.example.budgetkitaapp.map.saveLocation;

import java.io.Serializable;

public class userLocation implements Serializable {

    private String locationID;
    private String locationName;
    private String locationDetail;
    private double latitude;
    private double longitude;

    // Default constructor for Firebase
    public userLocation() {
    }

    public userLocation(String locationName, String locationDetail, double latitude, double longitude) {
        this.locationName = locationName;
        this.locationDetail = locationDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public userLocation(String locationID, String locationName, String locationDetail, double latitude, double longitude) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.locationDetail = locationDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }
}

