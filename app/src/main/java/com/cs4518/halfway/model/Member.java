package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Member {
    Location coordinates;
    public String username;

    public Member() {}

    public Member(String username) {
        this.username = username;
    }
}
