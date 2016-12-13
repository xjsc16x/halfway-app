package com.cs4518.halfway.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.google.android.gms.location.places.Place;

/**
 * Custom {@link #ViewHolder} for displaying places.
 *
 * @see Place
 * @see PlaceAdapter
 */
public class PlaceViewHolder extends RecyclerView.ViewHolder {
    /** TextView for displaying a place's name. */
    public final TextView placeNameText;
    /** TextView for displaying a place's address. */
    public final TextView placeAddressText;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        placeNameText = (TextView) itemView.findViewById(R.id.place_name);
        placeAddressText = (TextView) itemView.findViewById(R.id.place_address);
    }
}
