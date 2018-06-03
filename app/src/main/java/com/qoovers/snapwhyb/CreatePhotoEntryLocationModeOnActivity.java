package com.qoovers.snapwhyb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.qoovers.snapwhyb.app.adapters.PlaceSuggestionAdapter;
import com.qoovers.snapwhyb.app.helpers.PhotosDatabaseHelper;
import com.qoovers.snapwhyb.app.helpers.PlaceHelper;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.app.models.Place;
import com.qoovers.snapwhyb.app.values.IntentResults;
import com.qoovers.snapwhyb.app.values.LocationModes;
import com.qoovers.snapwhyb.utils.Camera;
import com.qoovers.snapwhyb.utils.Device;
import com.qoovers.snapwhyb.utils.Storage;
import com.qoovers.snapwhyb.utils.Time;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatePhotoEntryLocationModeOnActivity extends AppCompatActivity
{
    protected static final String TAG = CreatePhotoEntryLocationModeOnActivity.class.getSimpleName();

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 120000; // 120s
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;

    private GeoDataClient mGeoDataClient;

    private PlaceDetectionClient mPlaceDetectionClient;

    private List<Place> places = new ArrayList<Place>();

    private String dateTimeNow;

    private InputMethodManager inputMethodManager;

    private CoordinatorLayout baseLayout;

    private Toolbar toolbar;

    private ImageView image;

    private TextView timestamp;

    private EditText fieldPlace;
    private EditText fieldAddress;
    private EditText fieldCountry;
    private EditText fieldMessage;

    private LinearLayout placeSuggestionButton;
    private TextView placeSuggestionTitle;

    private ConstraintLayout progressBarContainer;
    private ConstraintLayout form;

    private SlidingUpPanelLayout slidingUpPanel;

    private FloatingActionButton slidingUpPanelAnchorButton;

    private LinearLayout slidingUpPanelBar;

    private TextView slidingUpPanelBarTitle;

    private PhotosDatabaseHelper db;

    private MaterialDialog placeSuggestionDialog;
    private MaterialSimpleListAdapter placeSuggestionAdapter;

    private double latitude;
    private double longitude;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        getPlaceLikelihood();
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
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo_entry_location_mode_on);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initToolBar();
        initInputMethodManager();
        initPhotosDatabaseHelper();
        initLayoutComponents();
        initSlidingUpPanel();

        setDateTimeNow();
        renderLayout();

        slidingUpPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED:
                        renderExpandedSlidingUpPanelElements();
                        break;

                    case COLLAPSED:
                        renderCollapsedSlidingUpPanelElements();
                        break;

                    case ANCHORED:
                        renderAnchoredSlidingUpPanelElements();
                        break;

                    case HIDDEN:
                        renderHiddenSlidingUpPanelElements();
                        break;
                }
            }
        });
        slidingUpPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseSlidingUpPanel();
            }
        });

        slidingUpPanelAnchorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storePhoto();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPlaceLikelihood() {
        @SuppressWarnings("MissingPermission") final
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                    int count;
                    count = likelyPlaces.getCount();

                    if (count == 0) {
                        hidePlaceSuggestionButton();
                        showPlaceSuggestTitle();

                    } else if (count == 1) {
                        hidePlaceSuggestionButton();
                        hidePlaceSuggestTitle();

                    } else if (count > 1) {
                        int i = 0;
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            if (i == 0) {
                                setInputFields(placeLikelihood.getPlace().getName().toString(), placeLikelihood.getPlace().getAddress().toString());
                                latitude = placeLikelihood.getPlace().getLatLng().latitude;
                                longitude = placeLikelihood.getPlace().getLatLng().longitude;
                            }

                            Place place = new Place();
                            place.setName(placeLikelihood.getPlace().getName().toString());
                            place.setAddress(placeLikelihood.getPlace().getAddress().toString());
                            place.setLatitude(placeLikelihood.getPlace().getLatLng().latitude);
                            place.setLongitude(placeLikelihood.getPlace().getLatLng().longitude);
                            place.setLikelihood(placeLikelihood.getLikelihood());

                            places.add(place);

                            i++;
                        }
                    }

                    likelyPlaces.release();

                } else {
                    Log.e(TAG, "Exception: %s", task.getException());
                    hidePlaceSuggestionButton();
                    showPlaceSuggestTitle();
                }

                hideProgressBar();
                showForm();
            }
        });
    }

    private void openPlaceSuggestionDialog() {
        PlaceSuggestionAdapter adapter = new PlaceSuggestionAdapter(this, places);

        adapter.setOnItemClickListener(new PlaceSuggestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                String place = places.get(position).getName();
                String fullAddress = places.get(position).getAddress();

                setInputFields(place, fullAddress);

                latitude = places.get(position).getLatitude();
                longitude = places.get(position).getLongitude();

                placeSuggestionDialog.dismiss();
            }
        });

        placeSuggestionDialog = new MaterialDialog.Builder(this)
                .title(R.string.create_photo_entry_place_suggestion_dialog_title)
                .adapter(adapter, null)
                .show();
    }

    /**
     *
     * Methods
     *
     */

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setPadding(0, Device.getStatusBarHeight(this), 0, 0);
    }

    private void initInputMethodManager() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initPhotosDatabaseHelper() {
        db = new PhotosDatabaseHelper(this);
    }

    private void setDateTimeNow() {
        dateTimeNow = Time.getDateTime((int) Time.getUnixTimestamp());
    }

    private void initLayoutComponents() {
        baseLayout = findViewById(R.id.base_layout);

        slidingUpPanelBar = findViewById(R.id.bottom_sliding_panel_bar);

        slidingUpPanelAnchorButton = findViewById(R.id.bottom_sliding_panel_anchor_button);

        image = findViewById(R.id.photo);

        timestamp = findViewById(R.id.timestamp);

        fieldPlace = findViewById(R.id.field_place_description);
        fieldAddress = findViewById(R.id.field_address);
        fieldCountry = findViewById(R.id.field_country);
        fieldMessage = findViewById(R.id.field_message);

        placeSuggestionButton = findViewById(R.id.place_suggestion_button);
        placeSuggestionTitle = findViewById(R.id.place_suggestion_title);

        progressBarContainer = findViewById(R.id.progress_bar_container);
        form = findViewById(R.id.form);

        slidingUpPanelBarTitle = findViewById(R.id.bottom_sliding_panel_bar_title);
    }

    private void initSlidingUpPanel() {
        slidingUpPanel = findViewById(R.id.sliding_layout);
    }

    private void renderLayout() {
        loadPhoto();
        timestamp.setText(dateTimeNow);
        showProgressBar();
        hideForm();
        collapseSlidingUpPanel();
    }

    private void expandSlidingUpPanel() {
        renderExpandedSlidingUpPanelElements();
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    private void renderExpandedSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        slidingUpPanelBarTitle.setVisibility(View.GONE);
        hideSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
    }

    private void collapseSlidingUpPanel() {
        renderCollapsedSlidingUpPanelElements();
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void renderCollapsedSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        slidingUpPanelBarTitle.setVisibility(View.VISIBLE);
        showSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
    }

    private void anchorSlidingUpPanel() {
        renderAnchoredSlidingUpPanelElements();
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
    }

    private void renderAnchoredSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(0.5f);
        slidingUpPanelBarTitle.setVisibility(View.VISIBLE);
        showSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
    }

    private void hideSlidingUpPanel() {
        renderHiddenSlidingUpPanelElements();
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void renderHiddenSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        hideSlidingUpPanelAnchorButton();
    }

    private void hideSlidingUpPanelAnchorButton() {
        slidingUpPanelAnchorButton.setVisibility(View.GONE);
    }

    private void showSlidingUpPanelAnchorButton() {
        slidingUpPanelAnchorButton.setVisibility(View.VISIBLE);
    }

    private void showSlidingUpPanelBar() {
        slidingUpPanelBar.setVisibility(View.VISIBLE);
    }

    private void hideSlidingUpPanelBar() {
        slidingUpPanelBar.setVisibility(View.GONE);
    }

    private void hideForm() {
        form.setVisibility(View.GONE);
    }

    private void showForm() {
        form.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarContainer.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hidePlaceSuggestionButton() {
        placeSuggestionButton.setVisibility(View.GONE);
    }

    private void showPlaceSuggestionButton() {
        placeSuggestionButton.setVisibility(View.VISIBLE);
    }

    private void hidePlaceSuggestTitle() {
        placeSuggestionTitle.setVisibility(View.GONE);
    }

    private void showPlaceSuggestTitle() {
        placeSuggestionTitle.setVisibility(View.VISIBLE);
    }

    private void loadPhoto() {
        File file = Camera.getTmpFile(this);
        // Do not cache the temp image, or else imageview will show the same image since temp image filename is not unique
        Glide.with(this)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(image);
    }

    private void setInputFields(String place, String fullAddress) {
        String address = PlaceHelper.getAddress(fullAddress);
        String country = PlaceHelper.getCountry(fullAddress);

        fieldPlace.setText(place);
        fieldAddress.setText(address);
        fieldCountry.setText(country);
    }

    private void setInputFields(String place, String address, String country) {
        fieldPlace.setText(place);
        fieldAddress.setText(address);
        fieldCountry.setText(country);
    }

    private int getCameraIntentResultCode() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("takeCameraPhotoIntentResultCode")) {
            return bundle.getInt("takeCameraPhotoIntentResultCode");
        }

        return 0;
    }

    private void storePhoto() {
        String fileName = Storage.generateUniqueFileName();
        Storage.savePhoto(this, fileName, getCameraIntentResultCode());

        String place = "null";
        String address = "null";
        String country = "null";
        String description = "null";

        if (!fieldPlace.getText().toString().trim().isEmpty()) {
            place = fieldPlace.getText().toString().trim();
        }

        if (!fieldAddress.getText().toString().trim().isEmpty()) {
            address = fieldAddress.getText().toString().trim();
        }

        if (!fieldCountry.getText().toString().trim().isEmpty()) {
            country = fieldCountry.getText().toString().trim();
        }

        if (!fieldMessage.getText().toString().trim().isEmpty()) {
            description = fieldMessage.getText().toString().trim();
        }

        Photo photo = new Photo();
        photo.setLocationMode(LocationModes.LOCATION_MODE_ON);
        photo.setPhotoFileName(fileName);
        photo.setPlace(place);
        photo.setAddress(address);
        photo.setCountry(country);
        photo.setLatitude(String.valueOf(latitude));
        photo.setLongitude(String.valueOf(longitude));
        photo.setDescription(description);

        int id = (int) db.store(photo);

        Intent result = new Intent();
        result.putExtra("result", IntentResults.RESULT_OK); // Create entry result code
        result.putExtra("id", id);
        result.putExtra("fileName", fileName);
        result.putExtra("place", place);
        result.putExtra("address", address);
        result.putExtra("country", country);
        result.putExtra("latitude", String.valueOf(latitude));
        result.putExtra("longitude", String.valueOf(longitude));
        result.putExtra("description", description);

        setResult(RESULT_OK, result);
        finish();
    }

    public void onClickPlaceSuggestionButton(View view) {
        openPlaceSuggestionDialog();
    }
}
