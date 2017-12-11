package com.csc285.android.z_track;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.UUID;

/**
 * Pager activity to swipe between Event Fragments
 */

public class EventPagerFragment extends Fragment implements EventFragment.Callbacks
{

//    private static final String EXTRA_EVENT_ID = "com.csc285.android.z_track.event_id";
    private static final String ARG_EVENT_ID = "event_id";

    private ViewPager mViewPager;
    private List<Event> mEvent;
    UUID crimeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crimeId = (UUID) UUID.fromString(getArguments().getString(ARG_EVENT_ID));
        mEvent = EventLab.get(getActivity()).getEvents();
//        setContentView(R.layout.activity_event_pager);
//
//        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_EVENT_ID);
//
//        mViewPager = (ViewPager) findViewById(R.id.event_view_pager);
//        mEvent = EventLab.get(this).getEvents();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
//
//            @Override
//            public Fragment getItem(int position) {
//                Event event = mEvent.get(position);
//                return EventFragment.newInstance(event.getmId(), false);
//            }
//            @Override
//            public int getCount() {
//                return mEvent.size();
//            }
//        });
//
//        for (int i = 0; i < mEvent.size(); i++) {
//            if (mEvent.get(i).getmId().equals(crimeId)) {
//                mViewPager.setCurrentItem(i);
//                break;
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_event_pager, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.event_view_pager);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Event event = mEvent.get(position);
                return EventFragment.newInstance(event.getmId(), false);
            }
            @Override
            public int getCount() {
                return mEvent.size();
            }
        });

        for (int i = 0; i < mEvent.size(); i++) {
            if (mEvent.get(i).getmId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        return v;
    }

//    public static Intent newIntent(Context packageContext, UUID eventId) {
//        Intent intent = new Intent(packageContext, EventPagerFragment.class);
//        intent.putExtra(EXTRA_EVENT_ID, eventId);
//        return intent;
//    }

    public static EventPagerFragment newInstance(UUID eventId)
    {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId.toString());
        EventPagerFragment fragment = new EventPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onEventUpdated(Event crime) {
    }

}
