package com.cs4518.halfway.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs4518.halfway.R;
import com.cs4518.halfway.views.PlaceViewHolder;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Adapter for
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {
    private ArrayList<Place> mDataset;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter(ArrayList<Place> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_place, parent, false);
        context = parent.getContext();
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        // When the PlaceViewHolder is bound to a position, this method updates the ViewHolder to
        // display properly.
        final Place currentPlace = mDataset.get(position);
        holder.placeNameText.setText(currentPlace.getName());
        holder.placeAddressText.setText(currentPlace.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = currentPlace.getWebsiteUri();
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void swap(ArrayList<Place> list) {
        if (mDataset != null) {
            mDataset.clear();
            mDataset.addAll(list);
        } else {
            mDataset = list;
        }
        notifyDataSetChanged();
    }
}