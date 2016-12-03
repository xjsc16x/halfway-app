package com.cs4518.halfway.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Invitation {
    public int groupId;
    public String userId;
    // might need to be String because
    // JSON doesn't support DateTime type
    // could try with DateTime
    public String sent;
    public boolean accepted;

    public Invitation() {

    }

    public Invitation(int groupId, String userId, String sent, boolean accepted) {
        this.groupId = groupId;
        this.userId = userId;
        this.sent = sent;
        this.accepted = accepted;
    }
}
