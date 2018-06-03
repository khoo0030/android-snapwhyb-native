package com.qoovers.snapwhyb.app.controllers;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class BaseController extends MultiDexApplication
{
    public static final String TAG = BaseController.class.getSimpleName();

    private static BaseController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(BaseController.this);
    }

    public static synchronized BaseController getInstance() {
        return mInstance;
    }
}
