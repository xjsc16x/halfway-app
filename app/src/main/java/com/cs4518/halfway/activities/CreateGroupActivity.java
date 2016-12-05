package com.cs4518.halfway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: the implementation of adding a group to firebase
public class CreateGroupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String GRP = "groups";
    private static final String USR = "users";

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ChildEventListener userListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    private EditText _groupNameText;
    private EditText _membersText;
    private Button _createButton;
    private EditText _locationText;
    private ToggleButton _useLocationToggle;
    private Button _changeTimeButton;
    private TextView _timeText;

    private String groupId;
    private String userId;
    private User currentUser;
    private String meetingTime;

    private int minute;
    private int hour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
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
                            // TODO: implement time picker
                            if (validate()) {
                                makeNewGroup(groupName, currentUser, meetingTime, location);
                                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                                intent.putExtra("GROUP_ID", groupId);
                                intent.putExtra("USE_LOCATION", _useLocationToggle.isChecked());
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Failed to create group",
                                        Toast.LENGTH_SHORT).show();
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

        _groupNameText = (EditText) findViewById(R.id.input_group_name);
        //_membersText = (EditText) findViewById(R.id.input_group_name);
        _createButton = (Button) findViewById(R.id.btn_create_group);
        _locationText = (EditText) findViewById(R.id.input_location);
        _useLocationToggle = (ToggleButton) findViewById(R.id.toggle_location);

        _timeText = (TextView) findViewById(R.id.meeting_time);
        _changeTimeButton = (Button) findViewById(R.id.btn_change_time);

        showTime(hour, minute);

        _changeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement time picker fragment
            }
        });


        _useLocationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // TODO: Use location
                if (isChecked) {
                    String hi = "TODO: use current location";
                    _locationText.setText(hi);
                } else {
                    _locationText.setText("");
                }
            }
        });
    }

    public void showTime(int hour, int min) {
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
            StringBuilder time = new StringBuilder().append(hour).append(" : ").append("0").append(min)
                    .append(" ").append(format);
            _timeText.setText(time);
            meetingTime = time.toString();
        }
        else {
            StringBuilder time = new StringBuilder().append(hour).append(" : ").append(min)
                    .append(" ").append(format);
            _timeText.setText(time);
            meetingTime = time.toString();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateMembersText() {
        //TODO
    }

    /**
     * Adds a new group to Firebase
     * @param groupName
     * @param creator currently is type User
     * @param meetingTime
     * @param location
     */
    private void makeNewGroup(String groupName, User creator,
                              String meetingTime, String location) {
//        Group group = new Group(groupId, groupName, creator, meetingTime, location);
//
//        mDatabase.child(GRP).child(groupId).setValue(group);
        groupId = mDatabase.child(GRP).push().getKey();
        Group group = new Group(groupId, groupName, creator, meetingTime, location);
        Map<String, Object> groupValues = group.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupId, groupValues);
        childUpdates.put("/user-groups/" + creator.userId + "/" + groupId, groupValues);

        mDatabase.updateChildren(childUpdates);
    }

    /**
     * Adds a user to the current group as a child in Firebase
     *
     * @param username
     * @param name
     * @param userId
     */
    private void addUser(String username, String name, String userId) {
        User member = new User(username, name, userId);

        mDatabase.child(GRP).child(groupId).child(userId).setValue(member);
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

    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO
    }

}
