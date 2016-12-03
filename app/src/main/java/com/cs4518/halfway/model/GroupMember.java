package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GroupMember {
    public String userId;
    public int groupId;
    public String user_location;

    public GroupMember() {

    }

    public GroupMember(int groupId, String userId, String user_location) {
        this.groupId = groupId;
        this.userId = userId;
        this.user_location = user_location;
    }
}
