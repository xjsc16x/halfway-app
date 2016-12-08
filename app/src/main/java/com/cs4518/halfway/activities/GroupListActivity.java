package com.cs4518.halfway.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.cs4518.halfway.R;

/**
 * Created by Silo on 12/3/16.
 */
public class GroupListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GroupListFragment();
    }
}