package com.csc285.android.z_track;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.csc285.android.z_track.RecordSensor.RecordSensor;
import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class ActivityFragment extends Fragment implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ActivityFragment";
    private static final String ARG_EVENT_ID = "event_id";
    private static final String DIALOG_MARKER = "DialogMarker";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_PHOTO_PERMISSIONS = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_TIME = 3;
    private static final String SAVED_ACTIVE = "active";
    private static final String SAVED_STARTED = "started";
    private static final String SAVED_RESUMED = "resumed";
    private static final String SAVED_SAVE = "save";
    private static final String SAVED_TIME = "time";
    private static final String SAVED_ROTATE = "rotated";
    private static final String SAVED_STATE= "state";
    FloatingActionButton mStartActivity;
    FloatingActionButton mPauseActivity;
    FloatingActionButton mResumeActivity;
    FloatingActionButton mStopActivity;

    private RecyclerView mStatsRecyclerView;
    private StatsAdapter mAdapter;

    private SensorManager mSensorManager;
    private Event mEvent;
    Handler timer, otherTasks;
    Time now;
    LocationA mTracking;
    Distance mDistance;
    Elevation mElevation;
    Pace mPace;
    Velocity mVelocity, mVelocityAvg, mHeading;
    RecordSensor accelR;
    Sensor accel, compass;
    File mPhotoFile;
    Polyline path;
    long mLastUpdateTime;
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private static final long sensorUpdateFreq = 500;
    private LocationRequest mLocationRequest;

    GoogleApiClient mClient;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;


    boolean started = false;
    boolean active = false;
    boolean save = false;
    boolean resumed = false;
    boolean rotated = false;
    String state = "misc";
    int marker_photo_idx = 0;
    int IMPORTNAT_INDEX = 0;

    private SharedPreferences mPrefs;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final String[] CAMERA_PERMISSION = new String[]{
            Manifest.permission.CAMERA
    };


    public static ActivityFragment newInstance(UUID eventId)
    {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId.toString());
        ActivityFragment fragment = new ActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            active = savedInstanceState.getBoolean(SAVED_ACTIVE);
            started = savedInstanceState.getBoolean(SAVED_STARTED);
            save = savedInstanceState.getBoolean(SAVED_SAVE);
            resumed = savedInstanceState.getBoolean(SAVED_RESUMED);
            rotated = savedInstanceState.getBoolean(SAVED_ROTATE);
            state = savedInstanceState.getString(SAVED_STATE);
        }

        mEvent = EventLab.get(getActivity()).getTempEvent();

        if (mEvent == null) {
            if (getArguments() != null) {
                UUID eventId = UUID.fromString(getArguments().getString(ARG_EVENT_ID));
                mEvent = EventLab.get(getActivity()).getEvent(eventId);
            } else {
                mEvent = new Event();
                EventLab.get(getActivity()).setTempEvent(mEvent);
            }
        }

        // Reference for SharedPreferences :
        // https://developer.android.com/reference/android/app/Activity.html#SavingPersistentState
        mPrefs = getActivity().getSharedPreferences("Event Pref", 0);
        active = mPrefs.getBoolean(SAVED_ACTIVE, false);
        save = mPrefs.getBoolean(SAVED_SAVE, false);
        started = mPrefs.getBoolean(SAVED_STARTED, false);
        resumed = mPrefs.getBoolean(SAVED_RESUMED, false);
        rotated = mPrefs.getBoolean(SAVED_ROTATE, false);
        state = mPrefs.getString(SAVED_STATE, "misc");

        mEvent.setAcType(state);

        mTracking = (LocationA)mEvent.getStat(R.string.activity_item_location);
        mDistance = (Distance)mEvent.getStat(R.string.activity_item_distance);
        mElevation = (Elevation)mEvent.getStat(R.string.activity_item_elevation);
        mPace = (Pace)mEvent.getStat(R.string.activity_item_pace);
        mVelocity = (Velocity)mEvent.getStat(R.string.activity_item_topspeed);
        mVelocityAvg = (Velocity)mEvent.getStat(R.string.activity_item_avgspeed);
        mHeading = (Velocity)mEvent.getStat(R.string.activity_item_heading);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelR = new RecordSensor();

//        registerSensors();
        timer = new Handler();
        otherTasks = new Handler();

        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mTracking.setCurrent(location);
//                Log.v(TAG, "IN ON LOCATION CHANGE");
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
//                Log.v(TAG, "Status changed: " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
//                Log.i(TAG, "PROVIDER ENABLED: " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
//                Log.e(TAG, "PROVIDER DISABLED: " + s);
            }
        };

        createLocationRequest();
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();

        if (!mClient.isConnected()){

            try {
                // Solved Callback problem
                // https://stackoverflow.com/questions/9007600/onlocationchanged-callback-is-never-called
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                mTracking.setCurrent(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } catch (SecurityException se){
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
            }
        }

        if (mStatsRecyclerView != null){
            updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (now != null) {
            now.setEndTime();
        }

        outState.putBoolean(SAVED_STARTED, started);
        outState.putBoolean(SAVED_SAVE, save);
        outState.putBoolean(SAVED_ACTIVE, active);
        outState.putString(SAVED_TIME, now.getQTime());
        outState.putBoolean(SAVED_RESUMED, resumed);
        outState.putBoolean(SAVED_ROTATE, rotated);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem choose = menu.findItem(R.id.action_choose);
        MenuItem marker = menu.findItem(R.id.act_add_marker);

        if (started) {
            choose.setVisible(false);
            marker.setVisible(true);
        } else {
            choose.setVisible(true);
            marker.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.act_add_marker) {
            if (hasLocationPermission()) {
                addMarkerImage();
            } else {
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
            }
            return true;
        }

        if (id == R.id.action_choose) {

            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_choose_activity, null);

            final RadioButton walk = (RadioButton) v.findViewById(R.id.walkRadioButton);
            final RadioButton bike = (RadioButton) v.findViewById(R.id.bikeRadioButton);
            final RadioButton misc = (RadioButton) v.findViewById(R.id.miscRadioButton);

            new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.choose_activity)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (walk.isChecked()){
                                        state = "walk";
                                    } else if (bike.isChecked()){
                                        state = "ke";
                                    } else if (misc.isChecked()){
                                        state = "misc";
                                    }

                                    mEvent.setAcType(state);
                                }
                            })
                    .create().show();

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

        mStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Recording
                EventLab.get(getActivity()).addEvent(mEvent);
                mTracking.setStart(mTracking.getCurrent());
                now.setOfficialSTime(now.getCurrentTime());
                started = true;
                active = true;
                save = false;
                resumed = false;
                updateTime();
                updateUX();
            }
        });

        mPauseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pause Recording
                active = false;
                save = false;
                resumed = false;
                updateTime();
                updateUX();
            }
        });

        mResumeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resume Recording
                active = true;
                save = false;
                resumed = true;
                updateTime();
                updateUX();
            }
        });

        mStopActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop Recording
                started = false;
                active = false;
                save = true;
                resumed = false;

                updateTime();
                updateUX();

                now.setOfficialTime();
                mVelocityAvg.setVelocities(mVelocity.getVelocities());
                mEvent.setmStats(mTracking, R.string.activity_item_location);
                mEvent.setmStats(mDistance, R.string.activity_item_distance);
                mEvent.setmStats(mElevation, R.string.activity_item_elevation);
                mEvent.setmStats(mPace, R.string.activity_item_pace);
                mEvent.setmStats(mVelocity, R.string.activity_item_topspeed);
                mEvent.setmStats(mVelocityAvg, R.string.activity_item_avgspeed);
                mEvent.setmStats(now, R.string.activity_item_time);
                mEvent.setmStats(mHeading, R.string.activity_item_heading);

//                System.out.println(mTracking.getMarkers_photo());

                EventLab.get(getActivity()).setTempEvent(null);
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, EventFragment.newInstance(mEvent.getmId(), true));
                fm.addToBackStack(null).commit();
            }
        });

        mStatsRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mStatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStatsRecyclerView.setNestedScrollingEnabled(false);
        updateUI();

        now = (Time)mEvent.getStat(R.string.activity_item_time);

        if (savedInstanceState != null) {
            now.setQTime(savedInstanceState.getString(SAVED_TIME));
        }

        // Old Issue solved from : https://stackoverflow.com/a/33525515
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // All methods for compatibility
        mNSV.fullScroll(NestedScrollView.FOCUS_UP);
        mNSV.scrollTo(0,mNSV.getTop());
        mNSV.smoothScrollTo(0,0);

        updateUX();
        updateTime();

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

            if (MainActivity.UNIT.equals("SI")){
                units = getResources().getStringArray(R.array.units_si);
            } else {
                units = getResources().getStringArray(R.array.units_en);
            }
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

            if (stat instanceof Time) {
                mDataTextView.setText(getString(R.string.time, now.getTimeMinutes(),
                        String.format(Locale.getDefault(), "%02d", now.getTimeSeconds()),
                        String.format(Locale.getDefault(), "%03d", now.getTimeMilli())));
            }

            if (stat instanceof Pace) {
                mDataTextView.setText("" + "" + mPace.getPace());
            }

            if (stat instanceof Velocity) {
                if (stat.getId() == R.string.activity_item_topspeed) {
                    mDataTextView.setText("" + "" +
                        String.format(Locale.getDefault(), "%.4f", mVelocity.getTopVelocity()));
                }
                if (stat.getId() == R.string.activity_item_avgspeed) {
                    if (mVelocityAvg.getVelocities().size() != 0)
                    mDataTextView.setText("" + "" +
                        String.format(Locale.getDefault(), "%.4f", mVelocityAvg.getVelocities().get(mVelocityAvg.getVelocities().size()-1)));
                    else
                        mDataTextView.setText("0.0");
                }
                // 0=North, 90=East, 180=South, 270=West
                if (stat.getId() == R.string.activity_item_heading) {
                    mDataTextView.setText("" + "" +
                            String.format(Locale.getDefault(), "%.1f", mHeading.getLatestHeading()));
                }
//                if (stat.getId() == R.string.activity_item_speed){
//                    mTitleTextView.setVisibility(View.GONE);
//                    mUnitsTextView.setVisibility(View.GONE);
//                    mDataTextView.setVisibility(View.GONE);
//                }
            }

            if (stat instanceof Distance) {
                mDataTextView.setText("" + "" +
                        String.format(Locale.getDefault(), "%.4f", mDistance.getTotalDistance()));
            }

            if (stat instanceof Elevation) {
                mDataTextView.setText("" + "" + mElevation.getLatestElevation());
            }

            if (stat instanceof LocationA) {
                if (stat.getId() == R.string.activity_item_location) {
                    mDataTextView.setText("" +
                            String.format(Locale.getDefault(), "%.2f", mTracking.getCurrent().getLatitude()) + "°, " +
                            String.format(Locale.getDefault(), "%.2f", mTracking.getCurrent().getLongitude()) + "°");
                }
//                if (stat.getId() == R.string.activity_item_markers){
//                    mTitleTextView.setVisibility(View.GONE);
//                    mUnitsTextView.setVisibility(View.GONE);
//                    mDataTextView.setVisibility(View.GONE);
//                }
//                if (stat.getId() == R.string.activity_item_path){
//                    mTitleTextView.setVisibility(View.GONE);
//                    mUnitsTextView.setVisibility(View.GONE);
//                    mDataTextView.setVisibility(View.GONE);
//                }
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

        void setmStats(List<Statistics> stats) { mStats = stats; }
    }

    private void updateEvent() {
        EventLab.get(getActivity()).updateEvent(mEvent);
    }

    private void updateUI() {
        List<Statistics> stats = mEvent.getmStats();

        if (mAdapter == null) {
            mAdapter = new StatsAdapter(stats);
            mStatsRecyclerView.setAdapter(mAdapter);
        } else {
            mStatsRecyclerView.setAdapter(mAdapter);
            mAdapter.setmStats(stats);
            mAdapter.notifyDataSetChanged();
        }

        updateEvent();
    }

    void updateUX(){
        getActivity().invalidateOptionsMenu();

        if(!started){
            unregisterSensors();
            mStartActivity.setVisibility(View.VISIBLE);
            mResumeActivity.setVisibility(View.GONE);
            mPauseActivity.setVisibility(View.GONE);
            mStopActivity.setVisibility(View.GONE);

        } else {
            if (active & !save) {
                registerSensors();
                mStartActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.VISIBLE);
                mStopActivity.setVisibility(View.VISIBLE);
            }

            if (!active & !save) {
                unregisterSensors();
                mStartActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.VISIBLE);
                mStopActivity.setVisibility(View.VISIBLE);
//                drawMarkers();
            }
        }

        updateUI();
        drawMarkers();
//        drawPath();
    }

    void updateTime(){
        if( active && !save && started && !resumed){
            now.setStartTime();
//            if(mClient.isConnected()) getLocation();
            timer.postDelayed(runnable, 0);
            otherTasks.postDelayed(importantRunnable,sensorUpdateFreq);
            accelR.setLastUpdate(now.getCurrentTime());
        }

        if( active && !save && started && resumed){
            if (!rotated) now.addDeltaTime();
            now.setStartTime();
            timer.postDelayed(runnable, 0);
            otherTasks.postDelayed(importantRunnable,sensorUpdateFreq);
            accelR.setLastUpdate(now.getCurrentTime());
        }

        if ( !active & !save && started){
            now.setEndTime();
            timer.removeCallbacks(runnable);
            otherTasks.removeCallbacks(importantRunnable);
        }

        if ( !active & save && started){
            started = false;
            now.setOfficialTime();
            now.setEndTime();
            timer.removeCallbacks(runnable);
            otherTasks.removeCallbacks(importantRunnable);
            now.resetDeltaTime();
//            now.setTimeSeconds(0);
//            now.setTimeMinutes(0);
//            now.setTimeMilli(0);
            EventLab.get(getActivity()).setTempEvent(null);
        }
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
            Time now = (Time)mEvent.getStat(R.string.activity_item_time);
            now.updateTime();
            timer.postDelayed(this, 0);
            updateUI();
        }

    };

    public Runnable importantRunnable = new Runnable() {

        public void run() {
//            if (started && (now.getCurrentTime()%10 == 0)){
                getGPSLocation();
                getElevation();
//            }
//            if (started && (now.getCurrentTime()%500 == 0)){
                mVelocityAvg.calculateAvg();
//            }
//            if ((now.getCurrentTime()%1000 == 0)) {
//                drawPath();
//            }
            otherTasks.postDelayed(this, sensorUpdateFreq);
        }

    };

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), CAMERA_PERMISSION[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.csc285.android.z_track.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateEvent();

            FragmentManager manager = getFragmentManager();
            ShowLargePictureFragment dialog = ShowLargePictureFragment
                    .newInstance(mEvent.getPhotoFilename(marker_photo_idx-1), mTracking.getMarkerTitles().get(marker_photo_idx-1));

            dialog.setTargetFragment(ActivityFragment.this, REQUEST_TIME);
            dialog.show(manager, DIALOG_MARKER);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    // Add Marker Function
//                    addMarkerImage();
                }
            case REQUEST_PHOTO_PERMISSIONS:
                if (hasCameraPermission()) {
                    // Take picture
                    takePicture();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
            if (mTracking.getCurrent() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(mTracking.getCurrent().getLatitude(),
                        mTracking.getCurrent().getLongitude()), 16.0f));
            }
        } catch (SecurityException xe) {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        String title = marker.getTitle();
        ArrayList<String> ms = mTracking.getMarkerTitles();
        int idx = 0;

        for (int i = 0; i < ms.size(); i++){
            if (ms.get(i).equals(title)){
                idx = i;
                break;
            }
        }

        FragmentManager manager = getFragmentManager();
        ShowLargePictureFragment dialog = ShowLargePictureFragment
                .newInstance(mEvent.getPhotoFilename(idx), ms.get(idx));

        dialog.setTargetFragment(ActivityFragment.this, REQUEST_TIME);
        dialog.show(manager, DIALOG_MARKER);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    public void addMarkerImage(){
        mEvent.addPhotoFilename(marker_photo_idx);
        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, marker_photo_idx);
        //System.out.println(currentLocation);

        if (mTracking.getCurrent() != null && mPhotoFile != null) {
            // Open Dialog to take picture;
//            long millis = now.getCurrentTime();
            mTracking.addMarkers(mTracking.getCurrent(), getString(R.string.time, now.getTimeMinutes(),
                    String.format(Locale.getDefault(), "%02d", now.getTimeSeconds()),
                    String.format(Locale.getDefault(), "%03d", now.getTimeMilli())),
                    mEvent.getPhotoFilename(marker_photo_idx)
            );
            marker_photo_idx++;

            takePicture();
        } else {
            Toast error = Toast.makeText(getContext(), getString(R.string.error_location), Toast.LENGTH_SHORT);
            error.show();
        }
    }

    public void takePicture(){
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureImage.resolveActivity(packageManager) != null) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.csc285.android.z_track.fileprovider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            List<ResolveInfo> cameraActivities = getActivity()
                    .getPackageManager().queryIntentActivities(captureImage,
                            PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo activity : cameraActivities) {
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            if (hasCameraPermission()) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            } else {
                requestPermissions(CAMERA_PERMISSION, REQUEST_PHOTO_PERMISSIONS);
            }
        }
    }

    public void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    // Google Maps API Marker Documentation
    // https://developers.google.com/maps/documentation/android-api/marker
    public void showMarker(Double lat, Double lon, String title, File imageFile) {
        if (imageFile.exists()) {
//            mMap.clear();
            // Create a LatLng object with the given Latitude and Longitude
            LatLng markerLoc = new LatLng(lat, lon);

            //Add marker to map
            if (!active) {
                Bitmap bitmap = PictureUtils.getScaledBitmap(imageFile.getPath(), 200, 200);
                mMap.addMarker(new MarkerOptions()
                        .position(markerLoc)                  // at the location you needed
                        .title(title)                         // with a title you needed
                        .snippet("")     // and also give some summary of available
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                ); // and give your animation drawable as icon
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(markerLoc)                  // at the location you needed
                        .title(title)                         // with a title you needed
                        .snippet("")     // and also give some summary of available
//                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                ); // and give your animation drawable as icon
            }
        }
    }

    // Method taken from :
    // https://stackoverflow.com/questions/30249920/how-to-draw-path-as-i-move-starting-from-my-current-location-using-google-maps
    private void drawPath(){

        if (mMap != null) {
//            mMap.clear();  //clears all Markers and Polylines

//            for(int i = 0; i < IMPORTNAT_INDEX; i++) {
//            mDistance.clear();
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                if (mTracking.getPath().size() > 0) {
                    LatLng point = mTracking.getPath().get(IMPORTNAT_INDEX);
                    options.add(point);
                    if (IMPORTNAT_INDEX > 0) {
                        mDistance.updateDistance(Math.abs(mDistance.getDistanceFromLatLonInKm(mTracking.getPath().get(IMPORTNAT_INDEX - 1), point)));
                    }
                }
//            addMarkerImage(); //add Marker in current position
//            System.out.println("drewpath");
                path = mMap.addPolyline(options); //add Polyline
//            }

//            ArrayList<Location> m = mTracking.getMarkers();
//            ArrayList<String> ms = mTracking.getMarkerTitles();
////            ArrayList<String> mi = mEvent.getMarkers();
//
//
//            for (int i = 0; i < m.size(); i++) {
//                if (i < mEvent.getPhotoSize()) {
//                    File photoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, i);
//                    showMarker(m.get(i).getLatitude(), m.get(i).getLongitude(), ms.get(i), photoFile);
//                }
//            }
        }
    }

    public void drawMarkers(){
        ArrayList<Location> m = mTracking.getMarkers();
        ArrayList<String> ms = mTracking.getMarkerTitles();

        for (int i = 0; i < m.size(); i++) {
            if (i < mEvent.getPhotoSize()) {
                File photoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, i);
                showMarker(m.get(i).getLatitude(), m.get(i).getLongitude(), ms.get(i), photoFile);
            }
        }
    }

    public void getGPSLocation(){
        try {
            mTracking.setCurrent(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            LatLng latLng = new LatLng(mTracking.getCurrent().getLatitude(), mTracking.getCurrent().getLongitude());
            if (mTracking != null){
                mTracking.addToPath(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(mTracking.getCurrent().getLatitude(),
                        mTracking.getCurrent().getLongitude()), 18.0f));

                drawPath();
                IMPORTNAT_INDEX++;
                mElevation.setElevation(mTracking.getCurrent().getAltitude());
                mVelocity.setVelocity(mTracking.getCurrent().getSpeed());
//                mVelocityAvg.setVelocities(mVelocity.getVelocities());
//                System.out.println(mElevation.getLatestElevation());
            }
//            Log.i(TAG, "Called GPS Location");
        } catch (SecurityException xe){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    public void getElevation(){
        try {
            mTracking.setCurrent(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            if (mTracking != null){
                mElevation.setElevation(mTracking.getCurrent().getAltitude());
            }
//            Log.i(TAG, "Called Elevation");
        } catch (SecurityException xe){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    public void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mClient, mLocationRequest, this);
            Log.d(TAG, "Location update started ..............: " + pendingResult.toString());
        } catch (SecurityException xe){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

//    private void handleNewLocation(Location location) {
//        Log.d(TAG, location.toString());
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21));
//    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mTracking.setCurrent(location);
        IMPORTNAT_INDEX++;
        mLastUpdateTime = now.getCurrentTime();
        float accuracy = location.getAccuracy();
//        Log.d("iFocus", "The amount of accuracy is " + accuracy);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();


        LatLng latLng = new LatLng(latitude, longitude);
        if (mTracking != null){
            mTracking.addToPath(latLng);
        }
    }


    void registerSensors() {
        // Sensor Code abstracted from:
        // http://www.vogella.com/tutorials/AndroidSensor/article.html

//        mSensorManager.registerListener(this,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);

//        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (compass != null) {
            mSensorManager.registerListener(compassEventListener, compass,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
            Toast.makeText(getContext(), "ORIENTATION Sensor not found",
                    Toast.LENGTH_LONG).show();
        }

    }

    void unregisterSensors(){
        if (compass != null) {
            mSensorManager.unregisterListener(compassEventListener);
        }
    }

    private SensorEventListener compassEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
            float azimuth = event.values[0];
//            float azimuth1 = event.values[1];
//            float azimuth2 = event.values[2];
            mHeading.setHeading(azimuth*6);
//            Log.d(TAG, Float.toString(azimuth));
//            Log.d(TAG, Float.toString(azimuth1));
//            Log.d(TAG, Float.toString(azimuth2));
//            compassView.updateData(azimuth);
        }
    };

//    @Override
//    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Do something here if sensor accuracy changes.
//    }
//
//    @Override
//    public final void onSensorChanged(SensorEvent event) {
//        // The light sensor returns a single value.
//        // Many sensors return 3 values, one for each axis.
//        // Do something with this sensor value.
//
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            getAccelerometer(event);
//        }
//    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelerationSquareRoot >= 2) //
        {
            if (actualTime - accelR.getLastUpdate() < 200) {
                return;
            }
            accelR.setLastUpdate(actualTime);
//            Log.d(TAG, Float.toString(accelerationSquareRoot));
//            Log.d(TAG, Long.toString(actualTime));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

        if (started) {
//            timer.postDelayed(runnable, 0);
            if (active) registerSensors();
            otherTasks.postDelayed(importantRunnable, sensorUpdateFreq);
        }

//        mSensorManager.registerListener(this,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);
//        sm.registerListener(this, Sensor.TYPE_MAGNETIC_FIELD,
//                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
//
//        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventLab.get(getActivity()).setTempEvent(mEvent);
        unregisterSensors();

//        timer.removeCallbacks(runnable);
        otherTasks.removeCallbacks(importantRunnable);
        updateEvent();
//        mSensorManager.unregisterListener(this);
        if (mClient != null){
            if (mClient.isConnected()){
                stopLocationUpdates();
            }
        }

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean(SAVED_ACTIVE, active);
        ed.putBoolean(SAVED_SAVE, save);
        ed.putBoolean(SAVED_STARTED, started);
        ed.putBoolean(SAVED_RESUMED, resumed);
        ed.putString(SAVED_STATE, state);
        ed.putBoolean(SAVED_ROTATE, rotated);
        ed.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventLab.get(getActivity()).setTempEvent(mEvent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

// Reference to draw path on map
// https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/

//                    // Refresh Activity from activity
//                    // https://stackoverflow.com/questions/3053761/reload-activity-in-android
//                    getActivity().finish();
//                    startActivity(getActivity().getIntent());