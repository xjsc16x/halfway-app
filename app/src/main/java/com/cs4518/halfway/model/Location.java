package com.cs4518.halfway.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Location {
    public double latitude;
    public double longitude;

    public Location() {}

    public Location(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longitude = longtitude;
    }

    @Override
    public String toString() {
        return latitude +
                ", " + longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}
