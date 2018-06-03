package com.qoovers.snapwhyb.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qoovers.snapwhyb.R;
import com.qoovers.snapwhyb.app.models.Place;

import java.util.List;

public class PlaceSuggestionAdapter extends RecyclerView.Adapter<PlaceSuggestionAdapter.ViewHolder>
{
    private List<Place> places;

    private Context context;

    public PlaceSuggestionAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_place_suggestion, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Place places = this.places.get(position);

        TextView place = viewHolder.place;
        place.setText(places.getName());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView place;

        public ViewHolder(View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.place);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(itemView, getLayoutPosition());
            }
        }
    }

    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
