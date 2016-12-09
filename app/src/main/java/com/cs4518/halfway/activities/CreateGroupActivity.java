package com.cs4518.halfway.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.Invitation;
import com.cs4518.halfway.model.GroupMember;
import com.cs4518.halfway.model.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    private static final String GRP = "groups";
    private static final String USR = "users";
    private static final String INV = "invitations";
    private static final long GPS_UPDATE_DELAY = 5000;

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ChildEventListener userListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //private GoogleApiClient mGoogleApiClient;

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

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String groupId;
    private String userId;
    private String invitationId;
    private User currentUser;
    private String meetingTime;
    private String meetingDate;

    private int minute;
    private int hour;
    private int year, month, day;


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
                                makeNewGroup(groupName, currentUser, location);
                                sendInvitations(currentUser.name, groupName);
                                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                                intent.putExtra("GROUP_ID", groupId);
                                intent.putExtra("USE_LOCATION", _useLocationToggle.isChecked());
                                startActivity(intent);
                                finish();
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                _locationText.setText(location.getLatitude()+ " " + location.getLongitude());
            }
            @Override
            public void onStatusChanged(String str, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("PERMISSIONCHECK", "this is above version_code M");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
                return;
            }
        }
        else{
            Log.i("PERMISSIONCHECK", "this is Below version_code M");
            configureToggleButton();
        }

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        Log.i("onRPR", "recieved request code" + requestCode);
        switch(requestCode){

            case 10:

                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureToggleButton();
                return;
        }
    }

    private void configureToggleButton() {
        _useLocationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    try {
                        locationManager.requestLocationUpdates("gps", GPS_UPDATE_DELAY, (float) 0, locationListener);
                    } catch (SecurityException e) {
                        Log.e("LOCATION","isChecked cant request location updates");

                    }
                }
                else{
                    try{
                        locationManager.removeUpdates(locationListener);
                    } catch (SecurityException e) {
                        Log.e("LOCATION"," not isChecked cant removeUpdates");

                    }
                }
            }
        });
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

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

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
    private void makeNewGroup(String groupName, User creator, String location) {
        groupId = mDatabase.child(GRP).push().getKey();
        // TODO: Actually use creator's location
        GroupMember creatorMember = new GroupMember(creator.username, 0, 0);
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

        if (TextUtils.isEmpty(location)) {
            _locationText.setError("Enter a location");
            valid = false;
        } else {
            _locationText.setError(null);
        }

        return valid;
    }

}
