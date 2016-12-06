package com.cs4518.halfway.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;

import java.util.ArrayList;

/**
 * @deprecated This is for reference only and is the old template for activities.
 */
public class MainGroupListActivity extends AppCompatActivity {


    /**
     * This is the list of groups the activity should be displaying
     */
    ArrayList<Group> groupList;
    /** List Adapter which is used to show the data in a list */
    //FirebaseListAdapter<Group> fireAdapter;
    /**
     * Button to add new group
     */
    FloatingActionButton addNewGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar ab = getSupportActionBar();
        // ab.setDisplayHomeAsUpEnabled(true);

        addNewGroupButton = (FloatingActionButton) findViewById(R.id.main_add_group_button);
        addNewGroupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createGroup();
                    }
                }
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_group_list);
    }

    /**
     * Method called when create group button is pressed
     */
    private void createGroup() {
        Log.d("DEZ", String.format("Creating a group"));
        Intent intent = new Intent(this, GroupCreationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_invite_notifications:
                handleAddInvite();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleAddInvite() {
        // TODO temp delete this
        Log.d("DEZ", "hey");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_group_list, menu);
        return true;
    }
}
