package com.qoovers.snapwhyb;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qoovers.snapwhyb.app.adapters.GalleryAdapter;
import com.qoovers.snapwhyb.app.helpers.PhotosDatabaseHelper;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.utils.Storage;

import java.util.ArrayList;
import java.util.List;

public class FragmentMainActivityPhotoGallery extends Fragment
{
    protected final String TAG = FragmentMainActivityPhotoGallery.class.getSimpleName();

    private List<Photo> photos = new ArrayList<Photo>();
    private GalleryAdapter galleryAdapter;

    private RecyclerView gallery;

    private PhotosDatabaseHelper db;

    ShowPhotoClickListener showPhotoClickCallback;

    public interface ShowPhotoClickListener {
        void onShowPhotoClick(int position, int id, int locationMode);
    }

    DeletePhotoClickListener deletePhotoClickCallback;

    public interface DeletePhotoClickListener {
        void onDeletePhotoClick(int photosCount);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        getAllPhotos();
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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            showPhotoClickCallback = (ShowPhotoClickListener) context;
            deletePhotoClickCallback = (DeletePhotoClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public FragmentMainActivityPhotoGallery() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new PhotosDatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main_gallery, container, false);

        gallery = view.findViewById(R.id.gallery);
        setupGallery();

        galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                int id = photos.get(position).getId();
                int locationMode = photos.get(position).getLocationMode();

                showPhotoClickCallback.onShowPhotoClick(position, id, locationMode);
            }
        });

        galleryAdapter.setOnItemLongClickListener(new GalleryAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int position) {
                final int id = photos.get(position).getId();
                final String fileName = photos.get(position).getPhotoFileName();

                showConfirmToDeletePhotoSnackBar(view, position, id, fileName);
            }
        });

        return view;
    }

    /**
     *
     * Methods
     *
     */

    private void setupGallery() {
        gallery.setHasFixedSize(true);
        gallery.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        galleryAdapter = new GalleryAdapter(getActivity(), getContext(), photos);
        gallery.setAdapter(galleryAdapter);
    }

    private void getAllPhotos() {
        photos.clear();
        photos.addAll(db.index());
        galleryAdapter.notifyDataSetChanged();
    }

    private void deletePhoto(int id) {
        db.destroy(id);
    }

    private void deletePhotoFromStorage(String fileName) {
        Storage.deletePhoto(fileName);
    }

    private void removePhotoFromGallery(int position) {
        photos.remove(position);

        galleryAdapter.notifyItemRemoved(position);
        galleryAdapter.notifyDataSetChanged();
    }

    private void deletePhotoCallBack() {
        int photosCount = db.index().size();

        if(photosCount == 0) {
            deletePhotoClickCallback.onDeletePhotoClick(photosCount);
        }
    }

    private void showConfirmToDeletePhotoSnackBar(View view, final int position, final int id, final String fileName) {
        Snackbar snackBar = Snackbar
                .make(view, R.string.fragment_main_gallery_confirm_delete_photo, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePhoto(id);

                        deletePhotoFromStorage(fileName);

                        removePhotoFromGallery(position);

                        deletePhotoCallBack();

                        showPhotoDeletedSnackBar(view);
                    }
                });

        snackBar.setActionTextColor(getResources().getColor(R.color.snackbar_action_button_confirm));
        snackBar.show();
    }

    private void showPhotoDeletedSnackBar(View view) {
        Snackbar snackBar = Snackbar.make(view, R.string.fragment_main_gallery_photo_deleted, Snackbar.LENGTH_LONG);

        View snackBarView = snackBar.getView();
        TextView snackBarText = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextColor(getResources().getColor(R.color.snackbar_text_success));

        snackBar.show();
    }

    /**
     *
     * Public Methods
     *
     */

    public void addPhoto(int id, String fileName, String place, String address, String country,
                                  String latitude, String longitude, String description) {
        Photo photo = new Photo();
        photo.setId(id);
        photo.setPhotoFileName(fileName);
        photo.setPlace(place);
        photo.setAddress(address);
        photo.setCountry(country);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
        photo.setDescription(description);

        photos.add(photo);

        galleryAdapter.notifyDataSetChanged();
    }

    public void updatePhoto(int position, String description) {
        photos.get(position).setDescription(description);

        galleryAdapter.notifyDataSetChanged();
    }
}
