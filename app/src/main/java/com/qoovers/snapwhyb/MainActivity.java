package com.qoovers.snapwhyb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.qoovers.snapwhyb.app.controllers.LocationController;
import com.qoovers.snapwhyb.app.helpers.PhotosDatabaseHelper;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.app.values.Fragments;
import com.qoovers.snapwhyb.app.values.IntentResults;
import com.qoovers.snapwhyb.app.values.MenuItems;
import com.qoovers.snapwhyb.app.values.Camera;
import com.qoovers.snapwhyb.utils.Network;
import com.qoovers.snapwhyb.utils.Permission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        FragmentMainActivityEmptyState.CameraButtonClickListener,
        FragmentMainActivityPhotoGallery.ShowPhotoClickListener,
        FragmentMainActivityPhotoGallery.DeletePhotoClickListener
{
    protected final String TAG = MainActivity.class.getSimpleName();

    private CoordinatorLayout baseLayout;

    private FloatingActionButton floatingActionButton;

    private Fragment fragmentEmptyState;
    private Fragment fragmentPhotoGallery;

    private FragmentManager fragmentManager;

    private PhotosDatabaseHelper db;

    private List<Photo> photos = new ArrayList<Photo>();

    private int photosCount;

    private final int REQUEST_CHECK_SETTINGS = 0x1;
    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000; // 5s
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (LocationController.isLocationModeEnabled()) {
            checkLocationSettings();
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). (when empty state fragment is replaced by recycler fragment)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        baseLayout = findViewById(R.id.base_layout);

        floatingActionButton = findViewById(R.id.floating_action_button);

        fragmentEmptyState = new FragmentMainActivityEmptyState();
        fragmentPhotoGallery = new FragmentMainActivityPhotoGallery();

        fragmentManager = getSupportFragmentManager();

        db = new PhotosDatabaseHelper(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraProcess();
            }
        });

        initializeLayout();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (LocationController.isLocationModeEnabled()) {
            menu.add(Menu.NONE, MenuItems.LOCATION_ON, Menu.NONE, R.string.menu_item_location_on)
                    .setIcon(R.drawable.ic_location_on_white)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add(Menu.NONE, MenuItems.LOCATION_OFF, Menu.NONE, R.string.menu_item_location_off)
                    .setIcon(R.drawable.ic_location_off_white)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        menu.add(Menu.NONE, MenuItems.CAMERA, Menu.NONE, R.string.menu_item_camera)
                .setIcon(R.drawable.ic_photo_camera_white)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MenuItems.CAMERA:
                onClickMenuButtonCamera();
                break;

            case MenuItems.LOCATION_OFF:
                onClickMenuButtonLocation();
                break;

            case MenuItems.LOCATION_ON:
                onClickMenuButtonLocation();
                break;
        }

        return false;
    }


    /**
     *
     * Activity Result
     *
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IntentResults.TAKE_CAMERA_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    if (LocationController.isLocationModeEnabled()) {
                        startCreatePhotoEntryLocationOnActivity(resultCode);
                    } else {
                        startCreatePhotoEntryLocationOffActivity(resultCode);
                    }
                }

                break;

            case IntentResults.STORE_PHOTO_ENTRY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getExtras().getInt("result") == IntentResults.RESULT_OK) {
                        onPhotoStored(
                                data.getExtras().getInt("id"),
                                data.getExtras().getString("fileName"),
                                data.getExtras().getString("place"),
                                data.getExtras().getString("address"),
                                data.getExtras().getString("country"),
                                data.getExtras().getString("latitude"),
                                data.getExtras().getString("longitude"),
                                data.getExtras().getString("description")
                        );
                    }
                }

                break;

            case IntentResults.READ_PHOTO_ENTRY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getExtras().getInt("result") == IntentResults.UPDATE_PHOTO_ENTRY) {
                        onPhotoUpdated(
                                data.getExtras().getInt("position"),
                                data.getExtras().getString("description")
                        );
                    }
                }

                break;

            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    LocationController.onLocationModeEnabled();
                    invalidateOptionsMenu();
                    showLocationOnSnackBar();
                } else {
                    LocationController.onLocationModeDisabled();
                    invalidateOptionsMenu();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void onPhotoStored(int id, String fileName, String place, String address, String country, String latitude, String longitude, String description) {
        if (photosCount == 0) {
            replaceEmptyStateWithGalleryLayout();
            photosCount++;
        } else {
            FragmentMainActivityPhotoGallery gallery = (FragmentMainActivityPhotoGallery) getSupportFragmentManager().findFragmentByTag(Fragments.TAG_PHOTO_GALLERY);
            gallery.addPhoto(id, fileName, place, address, country, latitude, longitude, description);
        }
    }

    private void onPhotoUpdated(int position, String description) {
        FragmentMainActivityPhotoGallery gallery = (FragmentMainActivityPhotoGallery) getSupportFragmentManager().findFragmentByTag(Fragments.TAG_PHOTO_GALLERY);
        gallery.updatePhoto(position, description);
    }

    /**
     *
     * Fragment Methods
     *
     */

    @Override
    public void onEmptyStateCameraButtonClick() {
        startCameraProcess();
    }

    @Override
    public void onShowPhotoClick(int position, int id, int locationMode) {
        startUpdatePhotoEntryActivity(position, id);
    }

    @Override
    public void onDeletePhotoClick(int photosCount) {
        if (photosCount == 0) {
            replaceGalleryWithEmptyStateLayout();
        }
    }

    /**
     *
     * Methods
     *
     */

    private void startCameraProcess() {
        if (!Permission.hasExternalStoragePermission(this)) {
            startPermissionStorageActivity();
        } else {
            if (LocationController.isLocationModeEnabled()) {
                if (!Permission.hasExternalStoragePermission(this)) {
                    startPermissionLocationActivity();
                } else {
                    if (!Network.isNetworkConnected(this)) {
                        showNetworkOffSnackBar();
                    } else {
                        checkLocationSettings(Camera.START_CAMERA_LOCATION_MODE_ON);
                    }
                }

            } else {
                startDeviceCamera();
            }
        }
    }

    private void startDeviceCamera() {
        Intent camera = com.qoovers.snapwhyb.utils.Camera.getCameraIntent(this);
        startActivityForResult(camera, IntentResults.TAKE_CAMERA_PHOTO);
    }

    private void startCreatePhotoEntryLocationOffActivity(int resultCode) {
        Intent intent = new Intent(this, CreatePhotoEntryLocationModeOffActivity.class);
        intent.putExtra("takeCameraPhotoIntentResultCode", resultCode);
        startActivityForResult(intent, IntentResults.STORE_PHOTO_ENTRY);
    }

    private void startCreatePhotoEntryLocationOnActivity(int resultCode) {
        Intent intent = new Intent(this, CreatePhotoEntryLocationModeOnActivity.class);
        intent.putExtra("takeCameraPhotoIntentResultCode", resultCode);
        startActivityForResult(intent, IntentResults.STORE_PHOTO_ENTRY);
    }

    private void startUpdatePhotoEntryActivity(int position, int id) {
        Intent intent = new Intent(this, UpdatePhotoEntryActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("id", id);
        startActivityForResult(intent, IntentResults.READ_PHOTO_ENTRY);
    }

    /**
     *
     * Layout Methods
     *
     */

    private void initializeLayout() {
        photos = db.index();

        photosCount = photos.size();

        if (photosCount > 0) {
            setGalleryLayout();
            showFloatingActionButton();
        } else {
            setEmptyStateLayout();
            hideFloatingActionButton();
        }
    }

    private void setEmptyStateLayout() {
        fragmentManager
                .beginTransaction().add(R.id.container, fragmentEmptyState, Fragments.TAG_EMPTY_STATE)
                .commit();

        hideFloatingActionButton();
    }

    private void setGalleryLayout() {
        fragmentManager
                .beginTransaction().add(R.id.container, fragmentPhotoGallery, Fragments.TAG_PHOTO_GALLERY)
                .commit();

        showFloatingActionButton();
    }

    private void replaceEmptyStateWithGalleryLayout() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragmentPhotoGallery, Fragments.TAG_PHOTO_GALLERY)
                .commitAllowingStateLoss();

        showFloatingActionButton();
    }

    private void replaceGalleryWithEmptyStateLayout() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragmentEmptyState, Fragments.TAG_EMPTY_STATE)
                .commitAllowingStateLoss();

        hideFloatingActionButton();
    }

    private void hideFloatingActionButton() {
        floatingActionButton.setVisibility(View.GONE);
    }

    private void showFloatingActionButton() {
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    /**
     *
     * Menu Item Methods
     *
     */

    private void onClickMenuButtonCamera() {
        startCameraProcess();
    }

    private void onClickMenuButtonLocation() {
        if (LocationController.isLocationModeEnabled()) {
            LocationController.onLocationModeDisabled();
            showLocationOffSnackBar();
            invalidateOptionsMenu();
        } else {
            LocationController.onLocationModeEnabled();
            setupLocationOnMode();
        }
    }

    private void showLocationOffSnackBar() {
        Snackbar.make(baseLayout, R.string.main_location_mode_off, Snackbar.LENGTH_LONG).show();
    }

    private void showLocationOnSnackBar() {
        Snackbar.make(baseLayout, R.string.main_location_mode_on, Snackbar.LENGTH_LONG).show();
    }

    private void showNetworkOffSnackBar() {
        Snackbar snackBar = Snackbar
                .make(baseLayout, R.string.main_network_off, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                });

        snackBar.show();
    }

    /**
     *
     * Permissions Methods
     *
     */

    private void setupLocationOnMode() {
        if (LocationController.isLocationModeEnabled() && !Permission.hasExternalStoragePermission(this)) {
            startPermissionLocationActivity();
        } else {
            checkLocationSettings();
        }
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

    /**
     *
     * Location Setting Methods
     *
     */

    protected void checkLocationSettings() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationController.onLocationModeEnabled();
                invalidateOptionsMenu();
                showLocationOnSnackBar();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LocationController.onLocationModeDisabled();

                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        //
                    }
                }
            }
        });
    }

    protected void checkLocationSettings(final String startCamera) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (startCamera.equals(Camera.START_CAMERA_LOCATION_MODE_ON)) {
                    startDeviceCamera();
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        //
                    }
                }
            }
        });
    }
}
