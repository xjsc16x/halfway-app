package com.cs4518.halfway.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

/**
 * Created by 10 on 2016-11-30.
 */
public class GroupCreationActivity extends AppCompatActivity {


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, GroupCreationActivity.class);
        //intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
}
