package com.cs4518.halfway.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GroupMember {
    public double longitude;
    public double latitude;
    public String username;

    public GroupMember() {}

    public GroupMember(String username, double longitude, double latitude) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("username", username);

        return result;
    }
}
