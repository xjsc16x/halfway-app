package com.cs4518.halfway;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainGroupListActivity extends AppCompatActivity {

    /*
    /** This is the list of groups the activity should be displaying
    ArrayList<Group> groupList;
    /** List Adapter which is used to show the data in a list
    FirebaseListAdapter<Group> fireAdapter;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar ab = getSupportActionBar();
        // ab.setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_group_list);
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
