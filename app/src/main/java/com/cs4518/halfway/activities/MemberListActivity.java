package com.cs4518.halfway.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MemberListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MemberListFragment();
    }
}
