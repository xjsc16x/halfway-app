package com.cs4518.halfway.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.google.android.gms.location.places.Place;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    public final TextView placeNameText;
    public final TextView placeAddressText;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        placeNameText = (TextView) itemView.findViewById(R.id.place_name);
        placeAddressText = (TextView) itemView.findViewById(R.id.place_address);
    }

//    public void bindMember(String placeName, String placeAddress) {
//        placeNameText.setText(placeName);
//        placeAddressText.setText(placeAddress);
//    }
}
