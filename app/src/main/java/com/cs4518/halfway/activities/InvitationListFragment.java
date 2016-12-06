package com.cs4518.halfway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Invitation;
import com.cs4518.halfway.model.InvitationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class InvitationListFragment extends Fragment {
    private static final String TAG ="InvitationListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Invitation, InvitationViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public InvitationListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_invitation_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.invitation_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
//        Query invitationQuery = getQuery(mDatabase);
        Query invitationQuery = mDatabase.child("user-invites")
                .child(getUid());
        mAdapter = new FirebaseRecyclerAdapter<Invitation, InvitationViewHolder>
                (Invitation.class, R.layout.list_item_invitation,
                InvitationViewHolder.class, invitationQuery) {

            @Override
            protected void populateViewHolder(final InvitationViewHolder viewHolder, final Invitation model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String groupId = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), GroupActivity.class);
                        intent.putExtra("GROUP_ID", groupId);
                        startActivity(intent);
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindInvitation(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // TODO: What to do when click accept
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // TODO: What to do when click decline
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
//
//    public abstract Query getQuery(DatabaseReference databaseReference);
}
