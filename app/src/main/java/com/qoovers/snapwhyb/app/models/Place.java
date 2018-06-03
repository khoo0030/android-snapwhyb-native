package com.qoovers.snapwhyb.app.models;

public class Place
{
    private String mName;
    private String mAddress;

    private double mLatitude;
    private double mLongitude;

    private float mLikelihood;

    public Place() {
        //
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public void setLikelihood(float likelihood) {
        this.mLikelihood = likelihood;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getLikelihood() {
        return mLikelihood;
    }
}
