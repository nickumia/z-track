package com.csc285.android.z_track;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class ActivityFragment extends Fragment implements SensorEventListener, OnMapReadyCallback {

    private static final String TAG = "ActivityFragment";
    FloatingActionButton mStartActivity;
    FloatingActionButton mPauseActivity;
    FloatingActionButton mResumeActivity;
    FloatingActionButton mStopActivity;

    private RecyclerView mStatsRecyclerView;
    private StatsAdapter mAdapter;
    private SensorManager mSensorManager;
    private StatisticsLab statsLab;
    private GoogleMap mMap;

    boolean active = false;
    boolean save = false;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    Handler timer;
    Time now;

    public static ActivityFragment newInstance()
    {
        return new ActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        timer = new Handler();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.act_add_marker) {
            if (hasLocationPermission()) {
                // Add Marker Function
            } else {
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity, container, false);

        mStartActivity = (FloatingActionButton) v.findViewById(R.id.start_activity);
        mPauseActivity = (FloatingActionButton) v.findViewById(R.id.pause_activity);
        mResumeActivity = (FloatingActionButton) v.findViewById(R.id.resume_activity);
        mStopActivity = (FloatingActionButton) v.findViewById(R.id.stop_activity);
        NestedScrollView mNSV = (NestedScrollView) v.findViewById(R.id.nsv);

        mPauseActivity.setVisibility(View.GONE);
        mResumeActivity.setVisibility(View.GONE);
        mStopActivity.setVisibility(View.GONE);

        mStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // Start Recording
                mStartActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.VISIBLE);
                mStopActivity.setVisibility(View.VISIBLE);
                active = true;
                save = false;
                now.setStartTime();
                timer.postDelayed(runnable, 0);
            }
        });

        mPauseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause Recording
                mPauseActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.VISIBLE);
                active = false;
                save = false;
                now.setEndTime();
                now.setDeltaTime();
                timer.removeCallbacks(runnable);
            }
        });

        mResumeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resume Recording
                mResumeActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.VISIBLE);
                active = true;
                save = false;
                now.setStartTime();
                timer.postDelayed(runnable, 0);
            }
        });

        mStopActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop Recording
                active = false;
                save = true;
                now.setEndTime();
                timer.removeCallbacks(runnable);
            }
        });

        mStatsRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mStatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStatsRecyclerView.setNestedScrollingEnabled(false);

        updateUI();
        now = (Time)statsLab.getStat(R.string.activity_item_time);

        // All methods for compatibility
        mNSV.fullScroll(NestedScrollView.FOCUS_UP);
        mNSV.scrollTo(0,mNSV.getTop());
        mNSV.smoothScrollTo(0,0);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;
    }

    private class StatsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mUnitsTextView;
        private TextView mDataTextView;
        private Statistics mStats;
        private String[] units;

        StatsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_stats_item, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.stats_title);
            mUnitsTextView = (TextView) itemView.findViewById(R.id.stats_units);
            mDataTextView = (TextView) itemView.findViewById(R.id.stats_data);

            units = getResources().getStringArray(R.array.units_en);

        }

        /** Bind
         * @param stat
         *
         * String Array Reference Page
         * https://developer.android.com/guide/topics/resources/string-resource.html#StringArray
         *
         */
        void bind(Statistics stat) {
            mStats = stat;

            mTitleTextView.setText(getResources().getString(stat.getId()));
            mUnitsTextView.setText(units[mStats.getIdx()]);

            if (stat instanceof Time){
                mDataTextView.setText("" + now.getTimeMinutes() + ":"
                        + String.format(Locale.getDefault(), "%02d", now.getTimeSeconds())
                        + ":" + String.format(Locale.getDefault(), "%03d", now.getTimeMilli()));
            }

        }

        @Override
        public void onClick(View view) {
//            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class StatsAdapter extends RecyclerView.Adapter<StatsHolder> {
        private List<Statistics> mStats;

        StatsAdapter(List<Statistics> stat) {
            mStats = stat;
        }

        @Override
        public StatsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new StatsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(StatsHolder holder, int position) {
            Statistics stat = mStats.get(position);
            holder.bind(stat);
        }

        @Override
        public int getItemCount() {
            return mStats.size();
        }
    }

    private void updateUI() {
        statsLab = StatisticsLab.get(getActivity());
        List<Statistics> stats = statsLab.getmStats();
        mAdapter = new StatsAdapter(stats);
        mStatsRecyclerView.setAdapter(mAdapter);

//        if (mMap == null || mMapImage == null) {
//            return;
//        }
//        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
//        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//
//        BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(mMapImage);
//        MarkerOptions itemMarker = new MarkerOptions()
//                .position(itemPoint)
//                .icon(itemBitmap);
//        MarkerOptions myMarker = new MarkerOptions()
//                .position(myPoint);
//        mMap.clear();
//        mMap.addMarker(itemMarker);
//        mMap.addMarker(myMarker);

//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(itemPoint)
//                .include(myPoint)
//                .build();
//        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
//        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
//        mMap.animateCamera(update);
    }


    /**
     * Used the following as a reference
     * Creating a Stopwatch
     * https://www.android-examples.com/android-create-stopwatch-example-tutorial-in-android-studio/
     *
     * Handler Reference Page
     * https://developer.android.com/reference/android/os/Handler.html
     */
    public Runnable runnable = new Runnable() {

        public void run() {
            Time now = (Time)statsLab.getStat(R.string.activity_item_time);
            now.updateTime();
            timer.postDelayed(this, 0);
            mAdapter.notifyDataSetChanged();
//            updateUI();
        }

    };


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float lux = event.values[0];
        // Do something with this sensor value.
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    // Add Marker Function
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        getActivity().invalidateOptionsMenu();
//        mClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        mClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



}
