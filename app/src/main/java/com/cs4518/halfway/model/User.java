package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String username;
    public String name;
    public String userId;
    public String email;

    public User() {
        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
    }

    public User(String username, String name, String userId) {
        this.username = username;
        this.name = name;
        this.userId = userId;
    }

}
