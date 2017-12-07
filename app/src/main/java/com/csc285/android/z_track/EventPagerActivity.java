package com.csc285.android.z_track;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Pager activity to swipe between Event Fragments
 */

public class EventPagerActivity extends AppCompatActivity implements EventFragment.Callbacks
{

    private static final String EXTRA_CRIME_ID = "com.csc285.android.z_track.crime_id";

    private ViewPager mViewPager;
    private List<Event> mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager)
                findViewById(R.id.event_view_pager);
        mEvent = EventLab.get(this).getEvents();
        FragmentManager fragmentManager =
                getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Event event = mEvent.get(position);
                return EventFragment.newInstance(event.getmId());
            }
            @Override
            public int getCount() {
                return mEvent.size();
            }
        });

        for (int i = 0; i < mEvent.size(); i++) {
            if
                    (mEvent.get(i).getmId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, EventPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }


    @Override
    public void onEventUpdated(Event crime) {
    }

}
