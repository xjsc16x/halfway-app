package com.cs4518.halfway.activities;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by 10 on 2016-12-07.
 */

public class LoginActivityMock extends LoginActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Do nothing
    }

    /**
     * Displays progress dialog and disables the log-in button.
     * <p>
     * Will continue until stopped by {@link #stopProgressDialog()}
     */
    @Override
    public void showProgressDialog() {
        // Do nothing
    }

    /**
     * Removes the progress dialog and re-enables the log-in button.
     */
    @Override
    public void stopProgressDialog() {
        // Do nothing
    }

    /**
     * Displays a toast message notifying user log-in failed
     */
    @Override
    public void showFailedToLoginToast() {
        // Do nothing
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    /**
     * Calls {@link TextView#setError(CharSequence)} on the email text field.
     */
    @Override
    public void setEmailErrorText(String errorText) {
        // Do nothing
    }

    /**
     * Calls {@link TextView#setError(CharSequence)} on the password text field.
     */
    @Override
    public void setPasswordErrorText(String errorText) {
        // Do nothing
    }

    /**
     * Retrieves currently entered text from password field
     *
     * @return The text entered in password field
     */
    @Override
    public String getPasswordText() {
        return "testpassword";
    }

    /**
     * Retrieves currently entered text from email field
     *
     * @return The text entered in email field.
     */
    @Override
    public String getEmailText() {
        return "testemail@halfway.com";
    }
}
