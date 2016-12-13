package com.cs4518.halfway.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;

public class GroupViewHolder extends RecyclerView.ViewHolder {
    public TextView mTimeTextView;
    public TextView mDateTextView;
    public TextView mGroupNameTextView;


    public GroupViewHolder(View itemView) {
        super(itemView);

        mGroupNameTextView = (TextView) itemView.findViewById(R.id.group_name);
        mDateTextView = (TextView) itemView.findViewById(R.id.group_date);
        mTimeTextView = (TextView) itemView.findViewById(R.id.group_time);
    }

    public void bindGroup(Group mGroup) {
        mGroupNameTextView.setText(mGroup.groupName);
        mTimeTextView.setText(mGroup.meetingTime);
        mDateTextView.setText(mGroup.meetingDate);
    }
}
