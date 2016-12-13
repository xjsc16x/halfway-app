package com.cs4518.halfway.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cs4518.halfway.R;

/**
 * Custom {@link #ViewHolder} for members of a group.
 * <p>
 * Only the username is displayed for a group member.
 *
 * @see com.cs4518.halfway.views.activities.GroupActivity
 */
public class MemberViewHolder extends RecyclerView.ViewHolder {
    public TextView memberUsername;

    public MemberViewHolder(View itemView) {
        super(itemView);

        memberUsername = (TextView) itemView.findViewById(R.id.member_username);
    }

    /**
     * Updates the view to display the given username.
     * <p>
     * This must be called once for the display to show anything.
     *
     * @param username Username to display.
     */
    public void bindMember(String username) {
        memberUsername.setText(username);
    }
}
