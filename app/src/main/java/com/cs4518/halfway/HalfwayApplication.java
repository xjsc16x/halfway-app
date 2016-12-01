package com.cs4518.halfway;

import android.app.Application;

/**
 * This is the master application class which sets the Firebase context.
 */
public class HalfwayApplication extends Application {

    public static final String FIREBASE_URL = "https://crackling-fire-3342.firebaseio.com/";

    @Override
    public void onCreate() {

        super.onCreate();
        //Firebase.setAndroidContext(this);
        // Call this to regenerate an Example Party
        // createData.run();
    }
}

