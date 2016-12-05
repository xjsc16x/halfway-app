package com.cs4518.halfway.activities;

import android.content.Context;
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

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.Group;
import com.cs4518.halfway.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

// TODO: the implementation of adding a group to firebase
public class CreateGroupActivity extends AppCompatActivity {
    private static final String EXTRA_GROUP_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ChildEventListener userListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText _groupNameText;
    private EditText _membersText;
    private Button _createButton;

    String groupId;
    private String userId;
    private Group _mGroup;
    private boolean isCreator;

    public static Intent newIntent(Context packageContext, String groupId) {
        Intent intent = new Intent(packageContext, CreateGroupActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupId = getIntent()
                .getStringExtra(EXTRA_GROUP_ID);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    mDatabase = FirebaseDatabase.getInstance()
                            .getReference();
                    /*
                    if(_mGroup != null ){
                        mDatabase.child("groups").child(_mGroup.groupID).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    _mGroup = dataSnapshot.getValue(Group.class);
                                    _groupNameText.setText(_mGroup.name);
                                    isCreator = (_mGroup.creator.equals(userId));
                                    updateMembersText();//TODO
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                        );
                    }
                    else {
                        makeNewGroup();
                    }
                    */ // this may be useful when we are editing groups but for now it is not necessary
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


        _createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mGroup.name = _groupNameText.getText().toString();
                mDatabase.child("groups").child(groupId).setValue(_mGroup);
            }
        });
        //mDatabase.child("users").child(userId).setValue(user);

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

    private void updateMembersText() {
        //TODO
    }

    private static void makeNewGroup(){
        //TODO
    }



}
