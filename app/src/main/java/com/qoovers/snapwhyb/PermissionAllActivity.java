package com.qoovers.snapwhyb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.qoovers.snapwhyb.app.values.Permissions;

public class PermissionAllActivity extends AppCompatActivity
{
    protected static final String TAG = PermissionAllActivity.class.getSimpleName();

    private boolean isStoragePermissionGranted =  false;
    private boolean isFineLocationPermissionGranted =  false;

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
        setContentView(R.layout.activity_permission_all);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case Permissions.STORAGE_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isStoragePermissionGranted = true;
                }
            }

            case Permissions.FINE_LOCATION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isFineLocationPermissionGranted = true;
                }
            }
        }

        permissionsGranted();
    }

    private void permissionsGranted() {
        if (isStoragePermissionGranted && isFineLocationPermissionGranted) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void grantAllPermissions(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Permissions.STORAGE_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.FINE_LOCATION_REQUEST_CODE);
    }
}
