package com.cs4518.halfway.activities;

import android.support.v4.app.Fragment;

public class InvitationListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new InvitationListFragment();
    }
}
