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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GroupListFragment extends Fragment{

    private String GL_DEBUG_TAG =" GroupListFragDebug";
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView mRecyclerView;

    private String userId;


    public void onCreate(Bundle savedInstanceState) {
        Log.i(GL_DEBUG_TAG,"creating grouplist fragment");
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        //mRecyclerView = (RecyclerView) getView().findViewById(R.id.group_recycler_view);

        firebaseAuth = FirebaseAuth.getInstance();
        Log.i(GL_DEBUG_TAG,"got fb auth");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference("/groups");
                } else {
                    //finish();
                    startActivity(new Intent(getActivity(),
                            LoginActivity.class));
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_group_list,container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        setUpFirebaseAdapter();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_group:
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Group, GroupHolder>
                (Group.class, R.layout.list_item_group, GroupHolder.class,
                        mDatabase) {

            @Override
            protected void populateViewHolder(GroupHolder viewHolder, Group model, int position) {
                viewHolder.bindGroup(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }


    public static class GroupHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final Context context;

        private TextView mTitleTextView;
        private TextView mDateTextView;

        private Group mGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_group_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_group_date_text_view);
        }

        public void bindGroup(Group group) {
            mGroup = group;
            mTitleTextView.setText(mGroup.groupName);
            mDateTextView.setText(mGroup.creator.name);
        }

        @Override
        public void onClick(View v) {
            /*
            //This opens a new intent of GroupActivity
            Intent intent =  new Intent(context, GroupActivity.class);
            context.startActivity(intent);
            */
        }
    }

}
