package com.cs4518.halfway.views.activities;

import android.support.v4.app.Fragment;

public class MemberListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MemberListFragment();
    }
}
