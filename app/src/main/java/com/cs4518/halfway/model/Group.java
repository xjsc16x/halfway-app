package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Group {
    public String groupID;
    public String name;
    public String creator;
    public String meetingTime;
    public String location;

    public Group() {

    }

    public Group(String groupID, String name ,String creator, String meetingTime, String location) {
        this.groupID = groupID;
        this.creator = creator;
        this.meetingTime = meetingTime;
        this.location = location;
    }
}
