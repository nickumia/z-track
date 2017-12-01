package com.csc285.android.z_track;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by nick on 12/1/2017.
 */

public class EventActivity extends SingleFragmentActivity {

    static final String EXTRA_EVENT_ID =
            "com.csc285.android.z_track.event_id";

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_EVENT_ID);
        return ActivityFragment.newInstance(crimeId);
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.fragment_activity;
    }
}
