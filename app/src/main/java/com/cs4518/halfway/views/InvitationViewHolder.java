package com.cs4518.halfway.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Invitation;
import com.cs4518.halfway.views.activities.InvitationListActivity;

/**
 * Custom {@link #ViewHolder} for Invitations.
 *
 * @see InvitationListActivity
 * @see Invitation
 */
public class InvitationViewHolder extends RecyclerView.ViewHolder {

    private TextView mCreatorTextView;
    private TextView mGroupNameTextView;
    private Button mAcceptButton;
    private Button mDeclineButton;

    public InvitationViewHolder(View itemView) {
        super(itemView);

        mCreatorTextView = (TextView) itemView.findViewById(R.id.group_creator);
        mGroupNameTextView = (TextView) itemView.findViewById(R.id.group_meeting_title);
        mAcceptButton = (Button) itemView.findViewById(R.id.btn_accept);
        mDeclineButton = (Button) itemView.findViewById(R.id.btn_decline);
    }

    /**
     * Updates this view to display the given invitation, and applies the given listeners to the
     * display as well.
     *
     * @param mInvitation          Given invitation to display.
     * @param acceptClickListener  Listener for the accept button.
     * @param declineClickListener Listener for the decline button.
     */
    public void bindInvitation(Invitation mInvitation,
                               View.OnClickListener acceptClickListener,
                               View.OnClickListener declineClickListener) {
        mCreatorTextView.setText(mInvitation.creator);
        mGroupNameTextView.setText(mInvitation.groupName);
        mAcceptButton.setOnClickListener(acceptClickListener);
        mDeclineButton.setOnClickListener(declineClickListener);
    }
}
