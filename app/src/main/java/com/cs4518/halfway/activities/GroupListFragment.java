package com.cs4518.halfway.activities;

/**
 * Created by Silo on 12/3/16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.GroupViewHolder;
import com.cs4518.halfway.model.Invitation;
import com.cs4518.halfway.model.InvitationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListFragment extends Fragment {
    private static final String TAG ="GroupListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Group, GroupViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public GroupListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_group_list, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.group_list);
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
        Query groupQuery = mDatabase.child("user-groups")
                .child(getUid());
        mAdapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>
                (Group.class, R.layout.list_item_group,
                        GroupViewHolder.class, groupQuery) {

            @Override
            protected void populateViewHolder(final GroupViewHolder viewHolder, final Group model, final int position) {
                final DatabaseReference groupRef = getRef(position);

                final String groupId = groupRef.getKey();

                viewHolder.bindGroup(model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupActivity.class);
                        intent.putExtra("GROUP_ID", groupId);
                        startActivity(intent);
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
}
