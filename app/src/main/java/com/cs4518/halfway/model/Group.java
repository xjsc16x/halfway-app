package com.cs4518.halfway.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// TODO: Incorporate other members
@IgnoreExtraProperties
public class Group {
    public String groupID;
    public String groupName;
    public GroupMember creator;
    public String meetingTime;
    public String location;
    public String meetingDate;

    public Group() {

    }

    public Group(String groupID, String groupName, GroupMember creator,
                 String meetingTime, String meetingDate, String location) {
        this.groupID = groupID;
        this.creator = creator;
        this.meetingTime = meetingTime;
        this.location = location;
        this.groupName = groupName;
        this.meetingDate = meetingDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupId", groupID);
        result.put("creator", creator);
        result.put("meetingTime", meetingTime);
        result.put("location", location);
        result.put("meetingDate", meetingDate);
        result.put("groupName", groupName);

        return result;
    }
}
