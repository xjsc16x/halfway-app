package com.cs4518.halfway.controllers;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.cs4518.halfway.views.activities.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginControllerTest {

    private static final String EMAIL = "testz@wpi.edu";
    private static final String PASSWORD = "topsecret";
    /** Main controller under test */
    LoginController controller;

    @Mock
    private LoginActivity successfulLoginActivity;
    @Mock
    private LoginActivity failedLoginActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(successfulLoginActivity.getEmailText()).thenReturn(EMAIL);
        when(successfulLoginActivity.getPasswordText()).thenReturn(PASSWORD);
        when(successfulLoginActivity.getApplicationContext()).thenReturn(InstrumentationRegistry.getTargetContext());
        controller = new LoginController(successfulLoginActivity);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.cs4518.halfway", appContext.getPackageName());
    }

    @Test
    public void testSetupActuallyWorked() throws Exception {
        // Simply tests that I set-up everything and that mocking is working
        assertNotNull(successfulLoginActivity);
        assertNotNull(controller);
        assertEquals(EMAIL, successfulLoginActivity.getEmailText());
        assertEquals(PASSWORD, successfulLoginActivity.getPasswordText());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        // Attempts to log-in and recieve authentication from Firebase.
        controller.login();
        verify(successfulLoginActivity).showProgressDialog();
        Thread.sleep(3000);
        verify(successfulLoginActivity).stopProgressDialog();
        verify(successfulLoginActivity).finish();
    }

    @Test
    public void testFailedLogin() throws Exception {
        // Attempts to log-in and fails.
        when(failedLoginActivity.getEmailText()).thenReturn(EMAIL);
        when(failedLoginActivity.getPasswordText()).thenReturn(PASSWORD + "b");
        LoginController l = new LoginController(failedLoginActivity);
        l.login();
        verify(failedLoginActivity).showProgressDialog();
        Thread.sleep(3000); // Giving Auth three seconds to log-in before checking if it worked.
        verify(failedLoginActivity).stopProgressDialog();
        verify(failedLoginActivity).showFailedToLoginToast();
        Mockito.verify(failedLoginActivity, never()).finish(); // Activity shouldn't have been called to finish
    }

    @Test
    public void testInvalidLogin() throws Exception {
        // Attempts a log-in with an email that doesn't resemble an email address
        LoginActivity act = Mockito.mock(LoginActivity.class);
        when(act.getEmailText()).thenReturn("notAnEmail");
        when(act.getPasswordText()).thenReturn("password");
        LoginController log = new LoginController(act);
        log.login();

        verify(act).showFailedToLoginToast();
        // verify(act, atLeastOnce()).setEmailErrorText();
        // Above can't be tested until externalized strings
        verify(act, never()).showProgressDialog();
        verify(act, never()).finish();

    }
}
