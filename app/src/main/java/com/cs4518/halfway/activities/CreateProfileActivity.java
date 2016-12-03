package com.cs4518.halfway.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cs4518.halfway.R;
import com.cs4518.halfway.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfileActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private EditText _nameText;
    private EditText _usernameText;
    private Button _finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        user = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        _nameText = (EditText) findViewById(R.id.input_name);
        _usernameText = (EditText) findViewById(R.id.input_username);
        _finishButton = (Button) findViewById(R.id.btn_finish);

        _finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewUser(_usernameText.getText().toString(), _nameText.getText().toString()
                    , user.getUid());
            }
        });

    }

    private void writeNewUser(String username, String name, String userId) {
        if (!validate()) {
            return;
        }

        User user = new User(username, name, userId);

        mDatabase.child("users").child(userId).setValue(user);

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
