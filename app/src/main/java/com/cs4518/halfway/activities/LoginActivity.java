package com.cs4518.halfway.activities;

import com.cs4518.halfway.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs4518.halfway.controllers.LoginController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

/**
 * Activity for login screen. Contains a link to (@link #SignUpActivityUp) activity is user doesn't have an account.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Tag for debugger logs
     */
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;


    /**
     * Rotating circle that displays when authenticating username log-in
     */
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new LoginController(this));

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

        });

    }

    /**
     * Displays progress dialog and disables the log-in button.
     * <p>
     * Will continue until stopped by {@link #stopProgressDialog()}
     */
    public void showProgressDialog() {
        _loginButton.setEnabled(false);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    /**
     * Removes the progress dialog and re-enables the log-in button.
     */
    public void stopProgressDialog() {
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
    }


    /**
     * Displays a toast message notifying user log-in failed
     */
    public void showFailedToLoginToast() {
        Toast.makeText(getApplicationContext(),
                "Failed to login",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * Calls {@link TextView#setError(CharSequence)} on the email text field.
     */
    public void setEmailErrorText(String errorText) {
        _emailText.setError(errorText);
    }

    /**
     * Calls {@link TextView#setError(CharSequence)} on the password text field.
     */
    public void setPasswordErrorText(String errorText) {
        _passwordText.setError(errorText);
    }

    /**
     * Retrieves currently entered text from password field
     *
     * @return The text entered in password field
     */
    public String getPasswordText() {
        return _passwordText.getText().toString();
    }

    /**
     * Retrieves currently entered text from email field
     *
     * @return The text entered in email field.
     */
    public String getEmailText() {
        return _emailText.getText().toString();
    }


}


