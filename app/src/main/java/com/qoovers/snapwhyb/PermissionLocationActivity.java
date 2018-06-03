package com.qoovers.snapwhyb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qoovers.snapwhyb.app.values.Permissions;
import com.qoovers.snapwhyb.utils.Permission;

public class PermissionLocationActivity extends AppCompatActivity
{
    protected static final String TAG = PermissionLocationActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_permission_location);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case Permissions.FINE_LOCATION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!Permission.hasExternalStoragePermission(this)) {
                        startPermissionStorageActivity();
                    } else {
                        startMainActivity();
                    }
                }
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

    public void grantLocationPermission(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.FINE_LOCATION_REQUEST_CODE);
    }
}
