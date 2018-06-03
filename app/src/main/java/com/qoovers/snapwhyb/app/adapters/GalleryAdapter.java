package com.qoovers.snapwhyb.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qoovers.snapwhyb.R;
import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.utils.Data;
import com.qoovers.snapwhyb.utils.Device;
import com.qoovers.snapwhyb.utils.Storage;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>
{
    protected static final String TAG = GalleryAdapter.class.getSimpleName();

    private List<Photo> photos;

    private Context context;

    private int gridHeight;

    public GalleryAdapter(Activity activity, Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;

        this.gridHeight = Device.getGridHeight(activity, context, 1.0, 2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_photo, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Photo photo = this.photos.get(position);

        RelativeLayout grid = viewHolder.grid;
        ImageView thumbnail = viewHolder.thumbnail;
        TextView description = viewHolder.description;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, gridHeight); // (width, height)
        grid.setLayoutParams(params);

        String fileName = photo.getPhotoFileName();
        File file = Storage.getPhoto(fileName);
        Glide.with(this.context)
                .load(file)
                .crossFade()
                .into(thumbnail);

        if(Data.isNull(photo.getDescription())) {
            description.setText(R.string.fragment_main_gallery_no_description);
        } else {
            description.setText(photo.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return this.photos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        RelativeLayout grid;
        ImageView thumbnail;
        TextView description;

        private ViewHolder(View itemView) {
            super(itemView);

            grid = itemView.findViewById(R.id.grid);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            description = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(itemView, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(itemView, getLayoutPosition());
                return true;
            }
            return false;
        }
    }

    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private static OnItemLongClickListener onItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}
