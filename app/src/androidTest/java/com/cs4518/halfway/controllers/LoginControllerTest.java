package com.cs4518.halfway.controllers;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.cs4518.halfway.activities.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private LoginActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(activity.getEmailText()).thenReturn(EMAIL);
        when(activity.getPasswordText()).thenReturn(PASSWORD);
        when(activity.getApplicationContext()).thenReturn(InstrumentationRegistry.getTargetContext());
        controller = new LoginController(activity);
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
        assertNotNull(activity);
        assertNotNull(controller);
        assertEquals(EMAIL, activity.getEmailText());
        assertEquals(PASSWORD, activity.getPasswordText());
    }

    @Test
    public void testLogin() throws Exception {
        // Attempts to log-in and recieve authentication from Firebase.
        controller.login();
        verify(activity).showProgressDialog();
        Thread.sleep(3000);
        verify(activity).stopProgressDialog();
        verify(activity).finish();
    }

}
