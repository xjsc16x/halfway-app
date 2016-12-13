package com.cs4518.halfway.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cs4518.halfway.views.activities.GroupListFragment;
import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;

/**
 * Custom {@link #ViewHolder} for Groups.
 *
 * @see GroupListFragment
 * @see Group
 */
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

    /**
     * Updates this view's display to show the given group.
     *
     * @param mGroup Group for the view to display.
     */
    public void bindGroup(Group mGroup) {
        mGroupNameTextView.setText(mGroup.groupName);
        mTimeTextView.setText(mGroup.meetingTime);
        mDateTextView.setText(mGroup.meetingDate);
    }
}
