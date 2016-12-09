package com.cs4518.halfway;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.cs4518.halfway.activities.LoginActivity;
import com.cs4518.halfway.activities.LoginActivityMock;
import com.cs4518.halfway.controllers.LoginController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginControllerTest {

    LoginActivity activity;
    LoginController controller;
    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        FirebaseApp.initializeApp(context);
        activity = new LoginActivityMock();
        controller = new LoginController(activity);
    }

    @Test
    public void testLogin() throws Exception {
        controller.onClick(null); // Presses the log-in button

    }
}