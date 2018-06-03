package com.qoovers.snapwhyb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qoovers.snapwhyb.app.helpers.PhotosDatabaseHelper;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.app.values.IntentResults;
import com.qoovers.snapwhyb.app.values.LocationModes;
import com.qoovers.snapwhyb.utils.Camera;
import com.qoovers.snapwhyb.utils.Device;
import com.qoovers.snapwhyb.utils.Storage;
import com.qoovers.snapwhyb.utils.Time;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatePhotoEntryLocationModeOffActivity extends AppCompatActivity
{
    protected static final String TAG = CreatePhotoEntryLocationModeOffActivity.class.getSimpleName();

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

    private SlidingUpPanelLayout slidingUpPanel;

    private FloatingActionButton slidingUpPanelAnchorButton;

    private LinearLayout slidingUpPanelBar;

    private TextView slidingUpPanelBarTitle;

    private PhotosDatabaseHelper db;

    private List<Photo> photo = new ArrayList<Photo>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        setContentView(R.layout.activity_create_photo_entry_location_mode_off);

        initToolBar();
        initInputMethodManager();
        initPhotosDatabaseHelper();
        initLayoutComponents();
        initSlidingUpPanel();

        setDateTimeNow();
        renderLayout();

        slidingUpPanel.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
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
        slidingUpPanel.setFadeOnClickListener(new OnClickListener() {
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

        slidingUpPanelBarTitle = findViewById(R.id.bottom_sliding_panel_bar_title);
    }

    private void initSlidingUpPanel() {
        slidingUpPanel = findViewById(R.id.sliding_layout);
    }

    private void renderLayout() {
        loadPhoto();
        timestamp.setText(dateTimeNow);
        collapseSlidingUpPanel();
    }

    private void expandSlidingUpPanel() {
        renderExpandedSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.EXPANDED);
    }

    private void renderExpandedSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        slidingUpPanelBarTitle.setVisibility(View.GONE);
        hideSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
        hideKeyboard();
    }

    private void collapseSlidingUpPanel() {
        renderCollapsedSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.COLLAPSED);
    }

    private void renderCollapsedSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        slidingUpPanelBarTitle.setVisibility(View.VISIBLE);
        showSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
        hideKeyboard();
    }

    private void anchorSlidingUpPanel() {
        renderAnchoredSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.ANCHORED);
    }

    private void renderAnchoredSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(0.5f);
        slidingUpPanelBarTitle.setVisibility(View.VISIBLE);
        showSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
        hideKeyboard();
    }

    private void hideSlidingUpPanel() {
        renderHiddenSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.HIDDEN);
    }

    private void renderHiddenSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        hideSlidingUpPanelAnchorButton();
        hideKeyboard();
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

    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(baseLayout.getWindowToken(), 0);
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
        photo.setLocationMode(LocationModes.LOCATION_MODE_OFF);
        photo.setPhotoFileName(fileName);
        photo.setPlace(place);
        photo.setAddress(address);
        photo.setCountry(country);
        photo.setLatitude("null");
        photo.setLongitude("null");
        photo.setDescription(description);

        int id = (int) db.store(photo);

        Intent result = new Intent();
        result.putExtra("result", IntentResults.RESULT_OK); // Create entry result code
        result.putExtra("id", id);
        result.putExtra("fileName", fileName);
        result.putExtra("place", place);
        result.putExtra("address", address);
        result.putExtra("country", country);
        result.putExtra("latitude", "");
        result.putExtra("longitude", "");
        result.putExtra("description", description);

        setResult(RESULT_OK, result);
        finish();
    }
}
