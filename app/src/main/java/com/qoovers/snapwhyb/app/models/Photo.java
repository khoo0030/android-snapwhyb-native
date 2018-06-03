package com.qoovers.snapwhyb.app.models;

public class Photo
{
    private int mId;
    private int mLocationMode;
    private int mCreateAt;
    private int mUpdatedAt;

    private String mPhotoFileName;
    private String mPlace;
    private String mAddress;
    private String mCountry;
    private String mLatitude;
    private String mLongitude;
    private String mDescription;

    public Photo() {
        //
    }

    /**
     *
     * Setters
     *
     */

    public void setId(int id) {
        this.mId = id;
    }

    public void setLocationMode(int mode) {
        this.mLocationMode = mode;
    }

    public void setCreatedAt(int createdAt) {
        this.mCreateAt = createdAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    public void setPhotoFileName(String $fileName) {
        this.mPhotoFileName = $fileName;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     *
     * Getters
     *
     */

    public int getId() {
        return mId;
    }

    public int getLocationMode() {
        return mLocationMode;
    }

    public int getCreatedAt() {
        return mCreateAt;
    }

    public int getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getPhotoFileName() {
        return mPhotoFileName;
    }

    public String getPlace() {
        return mPlace;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getDescription() {
        return mDescription;
    }
}
