package com.cs4518.halfway.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cs4518.halfway.R;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.cs4518.halfway.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";

    private static final String USR = "users";
    private FirebaseAuth firebaseAuth;

    private Button _createGroupBtn;
    private Button _logoutButton;
    private TextView _emailText;
    private Button _editProfileButton;
    private TextView _usernameText;
    private TextView _nameText;

    private String userId;
    private String email;

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private ChildEventListener userListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    email = user.getEmail();
                    _emailText.setText(email);
                    mDatabase = FirebaseDatabase.getInstance()
                            .getReference();
                    mDatabase.child(USR).child(userId).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    _nameText.setText(user.name);
                                    _usernameText.setText(user.username);
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

        _logoutButton = (Button) findViewById(R.id.buttonLogout);
        _emailText = (TextView) findViewById(R.id.user_email);
        _editProfileButton = (Button) findViewById(R.id.buttonEditProfile);
        _nameText = (TextView) findViewById(R.id.user_name);
        _usernameText = (TextView) findViewById(R.id.user_username);
        _createGroupBtn = (Button) findViewById(R.id.buttonCreateGroup);

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


        _editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });

        _createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
            }
        });

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
