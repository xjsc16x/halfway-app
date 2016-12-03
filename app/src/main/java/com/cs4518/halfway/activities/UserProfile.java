package com.cs4518.halfway.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cs4518.halfway.R;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserProfile extends AppCompatActivity {
    private static final String TAG = "UserProfile";
    private FirebaseAuth firebaseAuth;

    private Button _logoutButton;
    private TextView _emailText;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        user = firebaseAuth.getCurrentUser();

        _logoutButton = (Button) findViewById(R.id.buttonLogout);
        _emailText = (TextView) findViewById(R.id.input_name);

        _emailText.setText(user.getEmail());

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }

}
