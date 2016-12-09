package com.cs4518.halfway.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.GroupMember;
import com.cs4518.halfway.model.MemberViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MemberListFragment extends Fragment {
    private static final String TAG ="MemberListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<GroupMember, MemberViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public MemberListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_member_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.member_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
//        Query invitationQuery = getQuery(mDatabase);
        final Query memberQuery = mDatabase.child("groups")
                .child("-KYRLT6BDfjtRf-7_3u-").child("members");
        mAdapter = new FirebaseRecyclerAdapter<GroupMember, MemberViewHolder>
                (GroupMember.class, R.layout.list_item_member,
                        MemberViewHolder.class, memberQuery) {

            @Override
            protected void populateViewHolder(final MemberViewHolder viewHolder, final GroupMember model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                viewHolder.bindMember(memberRef.getKey());
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


}
