package com.cs4518.halfway.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs4518.halfway.R;
import com.cs4518.halfway.model.AddressResultReceiver;
import com.cs4518.halfway.model.Constants;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.GroupMember;
import com.cs4518.halfway.model.Location;
import com.cs4518.halfway.model.MemberViewHolder;
import com.cs4518.halfway.model.PlaceAdapter;
import com.cs4518.halfway.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;


public class GroupActivity extends AppCompatActivity
        implements OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener{
    private static final String GRP = "groups";
    private static final String TAG = "GroupActivity";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 10;
    private static final long INTERVAL = 300000; // 5 minute location requests

    private TextView groupNameText;
    private EditText locationText;
    private ToggleButton useLocationToggle;
    private TextView meetingTimeText;
    private Button changeTimeButton;
    private Button changeDateButton;
    private TextView meetingDateText;
    private Button updateLocationButton;

    private FirebaseRecyclerAdapter<GroupMember, MemberViewHolder> mAdapter;
    private RecyclerView mGroupMemberRecycler;
    private LinearLayoutManager mHorizontalManager;
    private RecyclerView mPlaceRecycler;
    private RecyclerView.LayoutManager mLayoutManager;

    private PlaceAdapter mPlaceAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AddressResultReceiver mResultReceiver;

    private FirebaseUser user;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private String userId;
    private String groupId;
    private User currentUser;
    private String username;

    private Group currentGroup;

    private RequestQueue requestQueue;
    private ArrayList<String> placeIDs;
    private ArrayList<Place> placesList;
    JsonObjectRequest jsonObjectRequest;

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
        mResultReceiver = new AddressResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                locationText.setText(mAddressOutput);
            }
        };

        buildGoogleApiClient();

        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("GROUP_ID");

        mPlaceRecycler = (RecyclerView) findViewById(R.id.place_list);
        mPlaceRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mPlaceRecycler.setLayoutManager(mLayoutManager);

        String getUrl = "http://halfway-server.herokuapp.com/api/placeids/" + groupId;
        mPlaceAdapter = new PlaceAdapter(new ArrayList<Place>());
        mPlaceRecycler.setAdapter(mPlaceAdapter);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            placeIDs = new ArrayList<>();
                            placesList = new ArrayList<>();
                            for(int i = 0; i < jsonArray.length();i++){
                                String nextPlaceID = jsonArray.getString(i);
                                placeIDs.add(nextPlaceID);
                                Log.i(TAG, "PlaceID found: " +nextPlaceID);
                                Places.GeoDataApi.getPlaceById(mGoogleApiClient, nextPlaceID)
                                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                            @Override
                                            public void onResult(PlaceBuffer places) {
                                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                    final Place myPlace = places.get(0).freeze();
                                                    placesList.add(myPlace);
                                                    Log.i(TAG, "Place found: " + myPlace.getName());

                                                } else {
                                                    Log.e(TAG, "Place not found");
                                                }

                                                mPlaceAdapter.swap(placesList);
                                                places.release();
                                            }
                                        });

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
                        Log.e("VOLLEY", "ERROR RECEIVING RESPONSE");
                    }
                });

        mGroupMemberRecycler = (RecyclerView) findViewById(R.id.member_list);
        mGroupMemberRecycler.setHasFixedSize(true);

        mHorizontalManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mHorizontalManager.setReverseLayout(true);
        mHorizontalManager.setStackFromEnd(true);
        mGroupMemberRecycler.setLayoutManager(mHorizontalManager);

        mDatabase = FirebaseDatabase.getInstance()
                .getReference();

        // Set up FirebaseRecyclerAdapter with the Query
        final Query memberQuery = mDatabase.child("group-members")
                .child(groupId);

        memberQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded");
                requestQueue.add(jsonObjectRequest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
                requestQueue.add(jsonObjectRequest);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                                                        String text = "Set your address";
                                                        locationText.setHint(text);
                                                    }
                                                    else {
                                                        startIntentService(0, location.latitude, location.longitude);
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
                            Location location = makeLocation(locationText.getText().toString());
                            if (location != null) {
                                mDatabase.child("group-members").child(groupId).
                                        child(username).child("location").setValue(location);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid address", Toast.LENGTH_SHORT).show();
                            }
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

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);

        memberQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mGroupMemberRecycler.setAdapter(mAdapter);

        configureToggleButton();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
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

    private void configureToggleButton() {

        useLocationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    startLocationUpdates();
                    Toast.makeText(getApplicationContext(),
                            "Update your location now", Toast.LENGTH_SHORT).show();
                }
                else {
                    stopLocationUpdates();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        else {
            //useLocationToggle.setChecked(true);
        }
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
        catch (SecurityException e) {
            // permission denied
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void startIntentService(int use_location, double latitude, double longitude) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        if (use_location == 1) {
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            startService(intent);
        }
        else {
            android.location.Location newLocation = new android.location.Location("");
            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, newLocation);
            startService(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //useLocationToggle.setChecked(true);
                } else {
                    useLocationToggle.setChecked(false);
                }
                return;
            }
        }
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
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

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                return;
            }
            startIntentService(1, 0, 0);
        }
    }

    private com.cs4518.halfway.model.Location makeLocation(String locationString) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(locationString, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                return new com.cs4518.halfway.model.Location(latitude, longitude);
            }
        }
        catch (IOException e) {}
        return null;
    }
}
