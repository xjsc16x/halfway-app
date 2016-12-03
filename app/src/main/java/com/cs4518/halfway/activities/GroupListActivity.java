package com.cs4518.halfway.activities;

import android.support.v4.app.Fragment;

/**
 * Created by Silo on 12/3/16.
 */

public class GroupListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GroupListFragment();
    }
}