package com.cs4518.halfway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class GroupActivity extends AppCompatActivity implements OnConnectionFailedListener{
    private static final String GRP = "groups";

    private TextView groupNameText;
    private EditText locationText;
    private ToggleButton useLocationToggle;
    private TextView meetingTimeText;
    private Button changeTimeButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private FirebaseUser user;

    private ChildEventListener groupListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    private String userId;
    private String groupId;
    private boolean useLocation;

    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    mDatabase = FirebaseDatabase.getInstance()
                            .getReference();

                    Intent intent = getIntent();
                    groupId = intent.getStringExtra("GROUP_ID");
                    // TODO: later need to get boolean when navigating from group list
                    // currently only getting extra from CreateGroupActivity
                    useLocation = intent.getBooleanExtra("GET_LOCATION", false);

                    mDatabase.child(GRP).child(groupId).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentGroup = dataSnapshot.getValue(Group.class);
                                    groupNameText.setText(currentGroup.groupName);
                                    locationText.setText(currentGroup.location);
                                    meetingTimeText.setText(currentGroup.meetingTime);

                                    if (user.getUid().equals(currentGroup.creator.userId)) {

                                        changeTimeButton = (Button) findViewById(R.id.btn_change_time);
                                        changeTimeButton.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );
                }
                else {
                    finish();
                    startActivity(new Intent(getApplicationContext(),
                            LoginActivity.class));
                }
            }

        };

        groupNameText = (TextView) findViewById(R.id.group_name);
        locationText = (EditText) findViewById(R.id.input_location);
        useLocationToggle = (ToggleButton) findViewById(R.id.toggle_location);
        meetingTimeText = (TextView) findViewById(R.id.meeting_time);

        useLocationToggle.setChecked(useLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {



        //TODO
    }

    @Override
    protected void onStart() {
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

}
