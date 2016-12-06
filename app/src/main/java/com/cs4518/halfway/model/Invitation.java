package com.cs4518.halfway.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Invitation {
    public String groupId;
    public String userId;
    public String creator;
    public String groupName;
    public String invitationId;

    public Invitation() {

    }

    public Invitation(String invitationId, String groupId, String userId, String groupName, String creator) {
        this.groupId = groupId;
        this.userId = userId;
        this.invitationId = invitationId;
        this.groupName = groupName;
        this.creator = creator;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("userId", userId);
        result.put("invitationId", invitationId);
        result.put("creator", creator);
        result.put("groupName", groupName);

        return result;
    }
}
