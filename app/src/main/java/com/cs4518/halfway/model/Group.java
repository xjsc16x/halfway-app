package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Group {
    public int groupID;
    public String creater;
    public String meetingTime;
    public String location;

    public Group() {

    }

    public Group(int groupID, String name, String meetingTime, String location) {
        this.groupID = groupID;
        this.creater = name;
        this.meetingTime = meetingTime;
        this.location = location;
    }
}
