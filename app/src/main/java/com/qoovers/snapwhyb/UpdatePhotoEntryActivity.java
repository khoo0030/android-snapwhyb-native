package com.qoovers.snapwhyb;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.qoovers.snapwhyb.app.helpers.PhotosDatabaseHelper;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.app.values.IntentResults;
import com.qoovers.snapwhyb.app.values.PhotoEntryModes;
import com.qoovers.snapwhyb.utils.Data;
import com.qoovers.snapwhyb.utils.Device;
import com.qoovers.snapwhyb.utils.Storage;
import com.qoovers.snapwhyb.utils.Time;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdatePhotoEntryActivity extends AppCompatActivity
{
    protected static final String TAG = UpdatePhotoEntryActivity.class.getSimpleName();

    private InputMethodManager inputMethodManager;

    private CoordinatorLayout baseLayout;

    private Toolbar toolbar;

    private ImageView image;

    private LinearLayout staticContainer;
    private ConstraintLayout form;
    private LinearLayout placeContainer;
    private LinearLayout messageContainer;
    private LinearLayout bottomSlidingPanelContent;

    private TextView place;
    private TextView address;
    private TextView country;
    private TextView message;
    private TextView timestamp;
    private TextView formTimestamp;

    private EditText fieldPlace;
    private EditText fieldAddress;
    private EditText fieldCountry;
    private EditText fieldMessage;

    private SlidingUpPanelLayout slidingUpPanel;

    private FloatingActionButton slidingUpPanelAnchorButton;

    private LinearLayout slidingUpPanelBar;

    private TextView slidingUpPanelBarTitle;

    private PhotosDatabaseHelper db;

    private List<Photo> photos = new ArrayList<Photo>();

    private String mode;
    private boolean isPhotoUpdated = false;

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
        switch (mode) {
            case PhotoEntryModes.EDIT:
                renderViewModeLayout();
                break;

            case PhotoEntryModes.VIEW:
                if(isPhotoUpdated) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", IntentResults.UPDATE_PHOTO_ENTRY); // Edit entry result code
                    resultIntent.putExtra("position", getGalleryPosition());
                    resultIntent.putExtra("description", photos.get(0).getDescription());
                    setResult(RESULT_OK, resultIntent);

                    finish();
                    super.onBackPressed();
                } else {
                    finish();
                    super.onBackPressed();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo_entry);

        initToolBar();
        initInputMethodManager();
        initPhotosDatabaseHelper();
        getPhotoData();
        initLayoutComponents();
        initSlidingUpPanel();

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
                switch (mode) {
                    case PhotoEntryModes.VIEW:
                        renderEditModeLayout();
                        break;

                    case PhotoEntryModes.EDIT:
                        updatePhoto();
                        break;
                }
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

    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(baseLayout.getWindowToken(), 0);
    }

    private int getPhotoId() {
        return getIntent().getExtras().getInt("id");
    }

    private int getGalleryPosition() {
        return getIntent().getExtras().getInt("position");
    }

    private void getPhotoData() {
        photos = db.show(getPhotoId());
    }

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

    private void initLayoutComponents() {
        baseLayout = findViewById(R.id.base_layout);

        slidingUpPanelBar = findViewById(R.id.bottom_sliding_panel_bar);

        slidingUpPanelAnchorButton = findViewById(R.id.bottom_sliding_panel_anchor_button);

        image = findViewById(R.id.photo);

        staticContainer = findViewById(R.id.static_container);
        form = findViewById(R.id.form);
        placeContainer = findViewById(R.id.place_container);
        messageContainer = findViewById(R.id.message_container);
        bottomSlidingPanelContent = findViewById(R.id.bottom_sliding_panel_content);

        timestamp = findViewById(R.id.timestamp);
        formTimestamp = findViewById(R.id.form_timestamp);

        fieldPlace = findViewById(R.id.field_place_description);
        fieldAddress = findViewById(R.id.field_address);
        fieldCountry = findViewById(R.id.field_country);
        fieldMessage = findViewById(R.id.field_message);

        place = findViewById(R.id.place_description);
        address = findViewById(R.id.address);
        country = findViewById(R.id.country);
        message = findViewById(R.id.message);

        slidingUpPanelBarTitle = findViewById(R.id.bottom_sliding_panel_bar_title);
    }

    private void initSlidingUpPanel() {
        slidingUpPanel = findViewById(R.id.sliding_layout);
    }

    private void renderLayout() {
        loadPhoto();
        renderViewModeElements();
        anchorSlidingUpPanel();

        mode = PhotoEntryModes.VIEW;
    }

    private void renderViewModeLayout() {
        switch (slidingUpPanel.getPanelState()) {
            case EXPANDED:
                anchorSlidingUpPanel();
                break;

            case COLLAPSED:
                collapseSlidingUpPanel();
                break;

            case ANCHORED:
                break;

            case HIDDEN:
                break;
        }

        renderViewModeElements();
        hideKeyboard();

        mode = PhotoEntryModes.VIEW;
    }

    private void renderViewModeElements() {
        hideForm();
        showStaticContainer();

        renderEditButton();

        if (Data.isNull(photos.get(0).getPlace()) && Data.isNull(photos.get(0).getAddress()) && Data.isNull(photos.get(0).getCountry())) {
            placeContainer.setVisibility(View.GONE);
        } else {
            placeContainer.setVisibility(View.VISIBLE);
        }

        if (Data.isNull(photos.get(0).getPlace())) {
            place.setVisibility(View.GONE);
        } else {
            place.setText(photos.get(0).getPlace());
        }

        if (Data.isNull(photos.get(0).getAddress())) {
            address.setVisibility(View.GONE);
        } else {
            address.setText(photos.get(0).getAddress());
        }

        if (Data.isNull(photos.get(0).getCountry())) {
            country.setVisibility(View.GONE);
        } else {
            country.setText(photos.get(0).getCountry());
        }

        timestamp.setText(Time.getDate(photos.get(0).getCreatedAt()));

        if (Data.isNull(photos.get(0).getDescription())) {
            messageContainer.setVisibility(View.GONE);
        } else {
            message.setText(photos.get(0).getDescription());
        }
    }

    private void renderEditModeLayout() {
        switch (slidingUpPanel.getPanelState()) {
            case EXPANDED:
                expandSlidingUpPanel();
                break;

            case COLLAPSED:
                expandSlidingUpPanel();
                break;

            case ANCHORED:
                expandSlidingUpPanel();
                break;

            case HIDDEN:
                break;
        }
        renderEditModeElements();
        hideKeyboard();

        mode = PhotoEntryModes.EDIT;
    }

    private void renderEditModeElements() {
        hideStaticContainer();
        showForm();

        renderSaveButton();

        if (Data.isNull(photos.get(0).getPlace())) {
            fieldPlace.setText("");
        } else {
            fieldPlace.setText(photos.get(0).getPlace());
        }

        if (Data.isNull(photos.get(0).getAddress())) {
            fieldAddress.setText("");
        } else {
            fieldAddress.setText(photos.get(0).getAddress());
        }

        if (Data.isNull(photos.get(0).getCountry())) {
            fieldCountry.setText("");
        } else {
            fieldCountry.setText(photos.get(0).getCountry());
        }

        formTimestamp.setText(Time.getDate(photos.get(0).getCreatedAt()));

        if (Data.isNull(photos.get(0).getDescription())) {
            fieldMessage.setText("");
        } else {
            fieldMessage.setText(photos.get(0).getDescription());
        }
    }

    private void hideStaticContainer() {
        staticContainer.setVisibility(View.GONE);
    }

    private void showStaticContainer() {
        staticContainer.setVisibility(View.VISIBLE);
    }

    private void hideForm() {
        form.setVisibility(View.GONE);
    }

    private void showForm() {
        form.setVisibility(View.VISIBLE);
    }

    private void expandSlidingUpPanel() {
        renderExpandedSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.EXPANDED);
    }

    private void renderExpandedSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(1.0f);
        slidingUpPanelBarTitle.setVisibility(View.GONE);
        expandBottomSlidingPanelContentPaddingTop();
        hideSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
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
    }

    private void anchorSlidingUpPanel() {
        renderAnchoredSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.ANCHORED);
    }

    private void renderAnchoredSlidingUpPanelElements() {
        slidingUpPanel.setAnchorPoint(0.5f);
        slidingUpPanelBarTitle.setVisibility(View.VISIBLE);
        reduceBottomSlidingPanelContentPaddingTop();
        showSlidingUpPanelBar();
        showSlidingUpPanelAnchorButton();
    }

    private void hideSlidingUpPanel() {
        renderHiddenSlidingUpPanelElements();
        slidingUpPanel.setPanelState(PanelState.HIDDEN);
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

    private void renderEditButton() {
        slidingUpPanelAnchorButton.setImageResource(R.drawable.ic_create_white);
        slidingUpPanelAnchorButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.anchor_button_edit)));
    }

    private void renderSaveButton() {
        slidingUpPanelAnchorButton.setImageResource(R.drawable.ic_save_white);
        slidingUpPanelAnchorButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.anchor_button_save)));
    }

    private void reduceBottomSlidingPanelContentPaddingTop() {
        int left = (int) getResources().getDimension(R.dimen.margin_3);
        int right = (int) getResources().getDimension(R.dimen.margin_3);
        int top = (int) getResources().getDimension(R.dimen.margin_6);
        int bottom = (int) getResources().getDimension(R.dimen.margin_10);

        bottomSlidingPanelContent.setPadding(left, top, right, bottom);
    }

    private void expandBottomSlidingPanelContentPaddingTop() {
        int left = (int) getResources().getDimension(R.dimen.margin_3);
        int right = (int) getResources().getDimension(R.dimen.margin_3);
        int top = (int) getResources().getDimension(R.dimen.margin_9);
        int bottom = (int) getResources().getDimension(R.dimen.margin_10);

        bottomSlidingPanelContent.setPadding(left, top, right, bottom);
    }

    private void loadPhoto() {
        File file = Storage.getPhoto(photos.get(0).getPhotoFileName());

        Glide.with(this)
                .load(file)
                .into(image);
    }

    private void updatePhoto() {
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
        photo.setId(getPhotoId());
        photo.setPlace(place);
        photo.setAddress(address);
        photo.setCountry(country);
        photo.setDescription(description);

        db.update(photo);

        isPhotoUpdated = true;

        photos.get(0).setPlace(place);
        photos.get(0).setAddress(address);
        photos.get(0).setCountry(country);
        photos.get(0).setDescription(description);

        renderViewModeLayout();
    }
}
