package com.cs4518.halfway.activities;

/**
 * Created by Silo on 12/3/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

public class GroupListFragment extends Fragment{

    //private FirebaseRecyclerView mfGroupRecyclerView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_group_list,container, false);

        //updateUI(); // Is this something we need to be doing?

        return view;
    }

    public void onResume() {
        super.onResume();
        //updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_group:
                Group group = new Group();
                //TODO ADD new group to firebase
                Intent intent = GroupCreationActivity
                        .newIntent(getActivity());
                startActivity(intent);
                return true;
            /*
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
             */ //we arent really using the subtitle for anything (maybe)
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GroupHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        private Group mGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_group_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_group_date_text_view);
        }

        public void bindGroup(Group group) {
            mGroup = group;
//            mTitleTextView.setText(mGroup.name);
//            mDateTextView.setText(mGroup.creator);
            mTitleTextView.setText(mGroup.groupName);
            String creator = "DEFAULT";
            mDateTextView.setText(creator);
            //mSolvedCheckBox.setChecked(mGroup.isSolved());
             //I think this will be made irrellevant by firebase
        }

        @Override
        public void onClick(View v) {
            Intent intent = GroupCreationActivity.newIntent(getActivity());
            startActivity(intent);
        }
    }
    private class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {

        private List<Group> mGroups;

        public GroupAdapter(List<Group> groups) {
            mGroups = groups;
        }

        @Override
        public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_group, parent, false);
            return new GroupHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupHolder holder, int position) {
            Group group = mGroups.get(position);
            holder.bindGroup(group);
        }

        @Override
        public int getItemCount() {
            return mGroups.size();
        }

        public void setGroups(List<Group> groups) {
            mGroups = groups;
        }
    }

}
