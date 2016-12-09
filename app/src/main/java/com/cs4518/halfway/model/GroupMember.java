package com.cs4518.halfway.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GroupMember {
    Location coordinates;
    public String username;

    public GroupMember() {}

    public GroupMember(String username, double longitude, double latitude) {
        this.username = username;
        this.coordinates = new Location(latitude, longitude);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
//        result.put("username", username);
        result.put("longitude", coordinates.getLongtitude());
        result.put("latitude", coordinates.getLatitude());

        return result;
    }
}
