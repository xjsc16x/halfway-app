package com.cs4518.halfway.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Invitation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvitationListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvitationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitationListFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private RecyclerView mInvitationRecyclerView;
    private InvitationAdapter mAdapter;

    private class InvitationHolder extends RecyclerView.ViewHolder {
        private TextView mCreatorTextView;
        private TextView mGroupNameTextView;
        private Button mAcceptButton;
        private Button mDeclineButton;

        private Invitation mInvitation;

        public InvitationHolder(View itemView) {
            super(itemView);

            mCreatorTextView = (TextView) itemView.findViewById(R.id.group_creator);
            mGroupNameTextView = (TextView) itemView.findViewById(R.id.group_meeting_title);
            mAcceptButton = (Button) itemView.findViewById(R.id.btn_accept);
            mDeclineButton = (Button) itemView.findViewById(R.id.btn_decline);
        }



        //TODO: need to use firebase and get user name based on userId and same for groupId
        public void bindInvitation(Invitation invitation) {
            mInvitation = invitation;
            mCreatorTextView.setText(mInvitation.userId);
            mGroupNameTextView.setText(mInvitation.groupId);
            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: set accept functionality
                }
            });
            mDeclineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: set decline functionality
                }
            });
        }

    }
    private class InvitationAdapter extends RecyclerView.Adapter<InvitationHolder> {

        @Override
        public InvitationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(InvitationHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
