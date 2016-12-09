package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Location {
    private double latitude;
    private double longtitude;

    public Location() {}

    public Location(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
}
