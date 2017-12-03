package com.csc285.android.z_track;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.csc285.android.z_track.RecordSensor.RecordSensor;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class ActivityFragment extends Fragment implements SensorEventListener, OnMapReadyCallback {

    private static final String TAG = "ActivityFragment";
    private static final String ARG_EVENT_ID = "event_id";
    FloatingActionButton mStartActivity;
    FloatingActionButton mPauseActivity;
    FloatingActionButton mResumeActivity;
    FloatingActionButton mStopActivity;

    private RecyclerView mStatsRecyclerView;
    private StatsAdapter mAdapter;

    private SensorManager mSensorManager;
    private Event mEvent;
    Handler timer;
    Time now;
    RecordSensor accelR, compassR;
    Sensor accel, compass;

    GoogleApiClient mClient;
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    Location currentLocation;

    boolean started = false;
    boolean active = false;
    boolean save = false;
//    int orient;

    private SharedPreferences mPrefs;


    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

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

        mEvent = EventLab.get(getActivity()).getTempEvent();

        if (mEvent == null) {
            if (getArguments() != null) {
                UUID crimeId = UUID.fromString(getArguments().getString(ARG_EVENT_ID));
                mEvent = EventLab.get(getActivity()).getEvent(crimeId);
            } else {
                mEvent = new Event();
                EventLab.get(getActivity()).setTempEvent(mEvent);
                System.out.println("I am a new event! :/");
            }
        } else {
            System.out.println("I am the same event! Yay!");
        }

        mPrefs = getActivity().getSharedPreferences("Event Pref", 0);
        active = mPrefs.getBoolean("active", false);
        save = mPrefs.getBoolean("save", false);
        started = mPrefs.getBoolean("started", false);

//        if (orient != getActivity().getResources().getConfiguration().orientation) {
//            started = true;
//        }

        setHasOptionsMenu(true);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelR = new RecordSensor();

        registerSensors();
        timer = new Handler();

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

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            };
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.act_add_marker) {
            if (hasLocationPermission()) {
                // Add Marker Function
                getLocation();
                if (currentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                            LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), 14.0f));
                } else {
                    Toast error = Toast.makeText(getContext(), getString(R.string.error_location), Toast.LENGTH_SHORT);
                    error.show();
                }

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

        updateUX();

        mStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Recording
                started = true;
                active = true;
                save = false;
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
                updateTime();
                updateUX();
            }
        });

        mStopActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop Recording
                active = false;
                save = true;
                updateTime();
                updateUX();
            }
        });

        mStatsRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mStatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStatsRecyclerView.setNestedScrollingEnabled(false);
        updateUI();

        now = (Time)mEvent.getStat(R.string.activity_item_time);

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

        void setmStats(List<Statistics> stats) { mStats = stats; }
    }

    private void updateEvent() {
        EventLab.get(getActivity()).updateEvent(mEvent);
//        mCallbacks.onEventUpdated(mEvent);
    }

    private void updateUI() {
        List<Statistics> stats = mEvent.getmStats();

        if (mAdapter == null) {
            mAdapter = new StatsAdapter(stats);
            mStatsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmStats(stats);
            mAdapter.notifyDataSetChanged();
        }

        if (mClient.isConnected()){
            getLocation();
        }

        updateEvent();
    }

    void updateUX(){
        if(!started){
            mStartActivity.setVisibility(View.VISIBLE);
            mResumeActivity.setVisibility(View.GONE);
            mPauseActivity.setVisibility(View.GONE);
            mStopActivity.setVisibility(View.GONE);

        } else {
            if (active & !save) {
                mStartActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.VISIBLE);
                mStopActivity.setVisibility(View.VISIBLE);
            }

            if (!active & !save) {
                mStartActivity.setVisibility(View.GONE);
                mPauseActivity.setVisibility(View.GONE);
                mResumeActivity.setVisibility(View.VISIBLE);
                mStopActivity.setVisibility(View.VISIBLE);
            }
        }

//        if ( !active & save && started){ }
    }

    void updateTime(){
        if( active & !save && started){
            now.setStartTime();
            timer.postDelayed(runnable, 0);
            accelR.setLastUpdate(now.getCurrentTime());
        }

        if ( !active & !save && started){
            now.setEndTime();
            now.addDeltaTime();
            timer.removeCallbacks(runnable);
        }

        if ( !active & save && started){
            started = false;
            now.setEndTime();
            timer.removeCallbacks(runnable);
            now.resetDeltaTime();
            now.setTimeSeconds(0);
            now.setTimeMinutes(0);
            now.setTimeMilli(0);
            // EventLab.get(getActivity()).setTempEvent(null);
        }
    }

    void registerSensors() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        if (compass != null) {
            mSensorManager.registerListener(compassEventListener, compass,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
            Toast.makeText(getContext(), "ORIENTATION Sensor not found",
                    Toast.LENGTH_LONG).show();
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
//            Log.d(TAG, Float.toString(azimuth));
//            compassView.updateData(azimuth);
        }
    };

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException xe) {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }


    public void getLocation(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
//            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request,
//                    new LocationListener() {
//                        @Override
//                        public void onLocationChanged(LocationA location) {
//                            currentLocation = location;
//                        }
//                    });
        }catch (SecurityException xe){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    public void showMarker(Double lat, Double lon) {
        mMap.clear();
        // Create a LatLng object with the given Latitude and Longitude
        LatLng markerLoc = new LatLng(lat, lon);

        //Add marker to map
        mMap.addMarker(new MarkerOptions()
                .position(markerLoc)                                                                        // at the location you needed
                .title("Desired Title")                                                                     // with a title you needed
                .snippet("Any summary if needed")                                                           // and also give some summary of available
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_act_add))); // and give your animation drawable as icon
    }

    public void getGPSLocation(){

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21));
    }
//
//    protected void createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    private void startLocationUpdates() {
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                mLocationCallback,
//                null /* Looper */);
//    }
//
//    private void stopLocationUpdates() {
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        // Do something with this sensor value.

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

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
            Log.d(TAG, Float.toString(accelerationSquareRoot));
            Log.d(TAG, Long.toString(actualTime));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, Sensor.TYPE_ACCELEROMETER,
//                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        sm.registerListener(this, Sensor.TYPE_MAGNETIC_FIELD,
//                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }

//        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
//        orient = getActivity().getResources().getConfiguration().orientation;
        if (now != null) {
//            now.setEndTime();
            now.addDeltaTime();
        }

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("active", active);
        ed.putBoolean("save", save);
        ed.putBoolean("started", started);
        ed.commit();

//        stopLocationUpdates();
        EventLab.get(getActivity()).updateEvent(mEvent);
        mSensorManager.unregisterListener(this);
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

//        SharedPreferences.Editor ed = mPrefs.edit();
//        ed.putBoolean("started", started);
//        ed.commit();

        if (compass != null) {
            mSensorManager.unregisterListener(compassEventListener);
        }
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