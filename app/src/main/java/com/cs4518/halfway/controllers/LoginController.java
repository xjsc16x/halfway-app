package com.cs4518.halfway.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cs4518.halfway.views.activities.LoginActivity;
import com.cs4518.halfway.views.activities.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * This is the controller for the {@link LoginActivity} class when the Log-in button is pressed.
 */
public class LoginController implements View.OnClickListener {

    /** Tag for debugger log */
    private static final String TAG = "LoginController";
    /** Reference to activity this controller is working with */
    private final LoginActivity activity;
    /** FirebaseAuth to interact with for logging in */
    private FirebaseAuth firebaseAuth;

    public LoginController(LoginActivity activity) {
        this.activity = Objects.requireNonNull(activity);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        login();
    }

    /**
     * Validates log-in text is entered, then verifies username and password using {@link
     * #firebaseAuth}.
     */
    protected void login() {
        Log.d(TAG, "Logging in");
        if (!validate()) {
            activity.showFailedToLoginToast();
            return;
        }

        activity.showProgressDialog();

        String email = activity.getEmailText();
        String password = activity.getPasswordText();

        // Line below was just changed, should be Listener(activity, new on...)
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Login attempt completed");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login was successful");
                            activity.stopProgressDialog();
                            activity.finish();
                            activity.startActivity(new Intent(activity.getApplicationContext(),
                                    UserProfileActivity.class));
                        } else {
                            Log.d(TAG, "Login was unsuccessful");
                            activity.showFailedToLoginToast();
                            activity.stopProgressDialog();
                        }
                    }
                });
    }


    /**
     * Determines if login information is a plausible log-in.
     * <p>
     * Also sets error text if there is an error. in either field.
     *
     * @return True if the log-in info looks valid.
     */
    private boolean validate() {
        boolean valid = true;

        String email = activity.getEmailText();
        String password = activity.getPasswordText();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            activity.setEmailErrorText("Enter a valid email address");
            valid = false;
        } else {
            activity.setEmailErrorText(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            activity.setPasswordErrorText("Must be greater than 6 alphanumeric characters");
            valid = false;
        } else {
            activity.setPasswordErrorText(null);
        }

        return valid;
    }
}
