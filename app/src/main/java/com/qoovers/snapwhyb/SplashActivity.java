package com.qoovers.snapwhyb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qoovers.snapwhyb.utils.Permission;

public class SplashActivity extends AppCompatActivity
{
    protected static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isStoragePermissionGranted = Permission.hasExternalStoragePermission(this);
        boolean isFineLocationPermissionGranted = Permission.hasAccessFineLocationPermission(this);

        if (isFineLocationPermissionGranted && isStoragePermissionGranted) {
            startMainActivity();
        } else {
            if (!isStoragePermissionGranted) {
                startPermissionStorageActivity();
            } else if (!isFineLocationPermissionGranted) {
                startPermissionLocationActivity();
            }
        }
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void startPermissionStorageActivity() {
        Intent i = new Intent(this, PermissionStorageActivity.class);
        startActivity(i);
        finish();
    }

    private void startPermissionLocationActivity() {
        Intent i = new Intent(this, PermissionLocationActivity.class);
        startActivity(i);
        finish();
    }
}
