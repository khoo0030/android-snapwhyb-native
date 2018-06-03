package com.qoovers.snapwhyb.app.controllers;

public class LocationController extends BaseController
{
    private static boolean isLocationModeEnabled;

    public static boolean isLocationModeEnabled() {
        return isLocationModeEnabled;
    }

    public static void onLocationModeEnabled() {
        isLocationModeEnabled = true;
    }

    public static void onLocationModeDisabled() {
        isLocationModeEnabled = false;
    }
}
