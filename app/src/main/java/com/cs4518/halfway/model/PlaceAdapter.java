package com.cs4518.halfway.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs4518.halfway.R;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by Tiffany on 12/9/2016.
 */


public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {
    private ArrayList<Place> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter(ArrayList<Place> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_place, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place currentPlace = mDataset.get(position);
        holder.placeNameText.setText(currentPlace.getName());
        holder.placeAddressText.setText(currentPlace.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}