package com.cs4518.halfway.activities;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.GroupMember;
import com.cs4518.halfway.model.Location;
import com.cs4518.halfway.model.MemberViewHolder;
import com.cs4518.halfway.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;


public class GroupActivity extends AppCompatActivity implements OnConnectionFailedListener{
    private static final String GRP = "groups";
    private static final String TAG = "GroupActivity";



    private TextView groupNameText;
    private EditText locationText;
    private ToggleButton useLocationToggle;
    private TextView meetingTimeText;
    private Button changeTimeButton;
    private Button changeDateButton;
    private TextView meetingDateText;
    private Button updateLocationButton;

    private FirebaseRecyclerAdapter<GroupMember, MemberViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private FirebaseUser user;

    private ChildEventListener groupListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    private String userId;
    private String groupId;
    private boolean useLocation;
    private User currentUser;
    private String username;

    private Group currentGroup;

    private RequestQueue requestQueue;
    private ArrayList<String> placeIDs;

    private int hour;
    private int minute;
    private String meetingTime, meetingDate;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,"http://halfway-server.herokuapp.com/api/placeids/-KYRISIIr0FyePR5LRa5",
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for(int i = 0; i < jsonArray.length();i++){
                                String nextPlaceID = jsonArray.getString(i);
                                placeIDs.add(nextPlaceID);
                            }
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("VOLLEY", "ERROR RECIEVING RESPONSE");
                    }
                });
        requestQueue.add(jsonObjectRequest);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("GROUP_ID");

        mRecycler = (RecyclerView) findViewById(R.id.member_list);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        mDatabase = FirebaseDatabase.getInstance()
                .getReference();

        // Set up FirebaseRecyclerAdapter with the Query
        final Query memberQuery = mDatabase.child("group-members")
                .child(groupId);
        mAdapter = new FirebaseRecyclerAdapter<GroupMember, MemberViewHolder>
                (GroupMember.class, R.layout.list_item_member,
                        MemberViewHolder.class, memberQuery) {

            @Override
            protected void populateViewHolder(final MemberViewHolder viewHolder,
                                              final GroupMember model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                viewHolder.bindMember(memberRef.getKey());
            }
        };

        // TODO: later need to get boolean when navigating from group list
        // currently only getting extra from CreateGroupActivity
        useLocation = intent.getBooleanExtra("GET_LOCATION", false);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();

                    mDatabase.child("users").child(userId).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentUser = dataSnapshot.getValue(User.class);
                                    username = currentUser.username;

                                    mDatabase.child("group-members").child(groupId).child(username).addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    GroupMember currentMember = dataSnapshot.getValue(GroupMember.class);
                                                    Location location = currentMember.location;
                                                    if (location.latitude == 0 && location.longitude == 0) {
                                                        String text = "Set your location coordinates";
                                                        locationText.setHint(text);
                                                    }
                                                    else {
                                                        locationText.setText(currentMember.location.toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );

                    updateLocationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: Error checking
                            String location = locationText.getText().toString();
                            String[] locationValues = location.split("[ ,]+");
                            mDatabase.child("group-members").child(groupId).
                                    child(username).child("location").child("latitude")
                                    .setValue(Double.parseDouble(locationValues[0]));
                            mDatabase.child("group-members").child(groupId).
                                    child(username).child("location").child("longitude")
                                    .setValue(Double.parseDouble(locationValues[1]));

                        }
                    });
                }
                else {
                    finish();
                    startActivity(new Intent(getApplicationContext(),
                            LoginActivity.class));
                }
            }

        };



        mDatabase.child(GRP).child(groupId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentGroup = dataSnapshot.getValue(Group.class);
                        groupNameText.setText(currentGroup.groupName);
//                        locationText.setText(currentGroup.location);
                        meetingTimeText.setText(currentGroup.meetingTime);
                        meetingDateText.setText(currentGroup.meetingDate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );



        groupNameText = (TextView) findViewById(R.id.group_name);
        locationText = (EditText) findViewById(R.id.input_location);
        useLocationToggle = (ToggleButton) findViewById(R.id.toggle_location);
        meetingTimeText = (TextView) findViewById(R.id.meeting_time);
        meetingDateText = (TextView) findViewById(R.id.meeting_date);
        updateLocationButton = (Button) findViewById(R.id.btn_update_location);



        useLocationToggle.setChecked(useLocation);

        memberQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecycler.setAdapter(mAdapter);
    }

    public void openTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        updateTime(hourOfDay, minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public void openDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        updateDate(year, monthOfYear, dayOfMonth);

                    }
                }, year, month, day);
        datePickerDialog.show();

    }

    public void updateDate(int year, int month, int day) {
        this.year = year;
        this.day = day;
        this.month = month;
        meetingDate = (month + 1) + "/"
                + day + "/" + year;

        updateDatabase();
    }

    public void updateTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if (minute < 10) {
            meetingTime = hour + ":0" + minute + " " + format;
        } else {
            meetingTime = hour + ":" + minute + " " + format;
        }

        updateDatabase();
    }

    public void updateDatabase() {
        Group group = new Group(currentGroup.groupID, currentGroup.groupName,
                currentGroup.creator, meetingTime, meetingDate);
        Map<String, Object> groupValues = group.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupId, groupValues);
        childUpdates.put("/user-groups/" + userId + "/" + groupId, groupValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: onConnectionFailed
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

}
