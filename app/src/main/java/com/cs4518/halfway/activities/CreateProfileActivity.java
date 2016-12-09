package com.cs4518.halfway.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private EditText _nameText;
    private EditText _usernameText;
    private Button _finishButton;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    _finishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            writeNewUser(_usernameText.getText().toString(), _nameText.getText().toString()
                                    , user.getUid());
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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        _nameText = (EditText) findViewById(R.id.input_name);
        _usernameText = (EditText) findViewById(R.id.input_username);
        _finishButton = (Button) findViewById(R.id.btn_finish);
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

    private void writeNewUser(String username, String name, String userId) {
        if (!validate()) {
            return;
        }

        User user = new User(username, name, userId);

        mDatabase.child("users").child(userId).setValue(user);
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(username).build();
//        this.user.updateProfile(profileUpdates);
//
//        Log.d(TAG, user.getDisplayName());

        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("Must be nonempty");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (username.isEmpty()) {
            _usernameText.setError("Must be nonempty");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        return valid;
    }
}
