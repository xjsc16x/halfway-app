package com.cs4518.halfway.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.AddressResultReceiver;
import com.cs4518.halfway.model.Constants;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.Invitation;
import com.cs4518.halfway.model.GroupMember;
import com.cs4518.halfway.model.User;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class CreateGroupActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String GRP = "groups";
    private static final String USR = "users";
    private static final String INV = "invitations";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 10;
    private static final long INTERVAL = 300000; // 5 minute location requests
    private static final String TAG = "CreateGroupActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ChildEventListener userListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;

    private EditText _groupNameText;
    private EditText _membersText;
    private Button _createButton;
    private EditText _locationText;
    private ToggleButton _useLocationToggle;
    private Button _changeTimeButton;
    private TextView _timeText;
    private Button _changeDateButton;
    private TextView _dateText;
    private TextView _addMembersText;

    private String groupId;
    private String userId;
    private String invitationId;
    private User currentUser;
    private String meetingTime;
    private String meetingDate;

    private int minute;
    private int hour;
    private int year, month, day;

    private LocationRequest mLocationRequest;
    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        mResultReceiver = new AddressResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                _locationText.setText(mAddressOutput);
            }
        };

        buildGoogleApiClient();
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    mDatabase = FirebaseDatabase.getInstance()
                            .getReference();
                    mDatabase.child(USR).child(userId).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentUser = dataSnapshot.getValue(User.class);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );

                    _createButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String groupName = _groupNameText.getText().toString();
                            String location = _locationText.getText().toString();

                            if (validate()) {
                                makeNewGroup(groupName, currentUser, makeLocation(location));
                                sendInvitations(currentUser.name, groupName);
                                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                                intent.putExtra("GROUP_ID", groupId);
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Failed to create group",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    finish();
                    startActivity(new Intent(getApplicationContext(),
                            LoginActivity.class));
                }
            }

        };

        _groupNameText = (EditText) findViewById(R.id.input_group_name);
        _addMembersText = (EditText) findViewById(R.id.input_members);
        _createButton = (Button) findViewById(R.id.btn_create_group);
        _locationText = (EditText) findViewById(R.id.input_location);
        _useLocationToggle = (ToggleButton) findViewById(R.id.toggle_location);

        _timeText = (TextView) findViewById(R.id.meeting_time);
        _dateText = (TextView) findViewById(R.id.meeting_date);
        _changeTimeButton = (Button) findViewById(R.id.btn_change_time);
        _changeDateButton = (Button) findViewById(R.id.btn_change_date);

        showTime(hour, minute);
        showDate(year, month, day);


        _changeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog();
            }
        });

        _changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog();
            }
        });

        configureToggleButton();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
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

    private void configureToggleButton() {

        _useLocationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    startLocationUpdates();
                }
                else {
                    stopLocationUpdates();
                }
            }
        });
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

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && _useLocationToggle.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void openTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        showTime(hourOfDay, minute);
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
                        showDate(year, monthOfYear, dayOfMonth);

                    }
                }, year, month, day);
        datePickerDialog.show();

    }

    public void showDate(int year, int month, int day) {
        this.year = year;
        this.day = day;
        this.month = month;
        meetingDate = (month + 1) + "/"
                + day + "/" + year;
        _dateText.setText(meetingDate);
    }

    public void showTime(int hour, int min) {
        this.hour = hour;
        minute = min;

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

        if (min < 10) {
            StringBuilder time = new StringBuilder().append(hour).append(":").append("0").append(min)
                    .append(" ").append(format);
            _timeText.setText(time);
            meetingTime = time.toString();
        }
        else {
            StringBuilder time = new StringBuilder().append(hour).append(":").append(min)
                    .append(" ").append(format);
            _timeText.setText(time);
            meetingTime = time.toString();
        }
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

    /**
     * Adds a new group to Firebase
     * @param groupName
     * @param creator currently is type User
     * @param location
     */
    private void makeNewGroup(String groupName, User creator, com.cs4518.halfway.model.Location location) {
        groupId = mDatabase.child(GRP).push().getKey();
        GroupMember creatorMember = new GroupMember(creator.username, location);
        Group group = new Group(groupId, groupName, creatorMember, meetingTime, meetingDate);
        Map<String, Object> groupValues = group.toMap();
        Map<String, Object> memberValues = creatorMember.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupId, groupValues);
        childUpdates.put("/user-groups/" + creator.userId + "/" + groupId, groupValues);
        childUpdates.put("/group-members/" + groupId + "/" + creator.username, memberValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void sendInvitations(String creator, String groupName) {
        String[] invitees = _addMembersText.getText().toString().toLowerCase().split("[ ,]+");

        invitationId = mDatabase.child(INV).push().getKey();
        Invitation invitation = new Invitation(invitationId, groupId, userId, groupName, creator);
        Map<String, Object> invitationValues = invitation.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        for (String invitee : invitees) {
            if (!invitee.isEmpty())
                childUpdates.put("/user-invites/" + invitee + "/" + invitationId, invitationValues);
        }

        mDatabase.updateChildren(childUpdates);
    }

    public boolean validate() {
        boolean valid = true;

        String groupName = _groupNameText.getText().toString();
        String location = _locationText.getText().toString();

        if (TextUtils.isEmpty(groupName)) {
            _groupNameText.setError("Enter a group name");
            valid = false;
        } else {
            _groupNameText.setError(null);
        }

        if (TextUtils.isEmpty(location) || makeLocation(location) == null) {
            _locationText.setError("Enter a valid address");
            valid = false;
        } else {
            _locationText.setError(null);
        }

        return valid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    _useLocationToggle.setChecked(true);
                } else {
                    _useLocationToggle.setChecked(false);
                }
                return;
            }
        }
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
//        else {
//            //_useLocationToggle.setChecked(true);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                return;
            }
            startIntentService();
        }
    }

}
