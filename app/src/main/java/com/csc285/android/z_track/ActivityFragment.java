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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.csc285.android.z_track.RecordSensor.RecordSensor;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class ActivityFragment extends Fragment implements
        SensorEventListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

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
    LocationA mTracking;
    RecordSensor accelR, compassR;
    Sensor accel, compass;
    File mPhotoFile;

    GoogleApiClient mClient;
    private GoogleMap mMap;
    Location currentLocation;


    boolean started = false;
    boolean active = false;
    boolean save = false;
    boolean resumed = false;
    String state = "";
    int marker_photo_idx = 0;

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
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState != null) {
            active = savedInstanceState.getBoolean(SAVED_ACTIVE);
            started = savedInstanceState.getBoolean(SAVED_STARTED);
            save = savedInstanceState.getBoolean(SAVED_SAVE);
            resumed = savedInstanceState.getBoolean(SAVED_RESUMED);
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

//        mEvent.addPhotoFilename(marker_photo_idx);
//        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, marker_photo_idx);

        // Reference for SharedPreferences :
        // https://developer.android.com/reference/android/app/Activity.html#SavingPersistentState
        mPrefs = getActivity().getSharedPreferences("Event Pref", 0);
        active = mPrefs.getBoolean(SAVED_ACTIVE, false);
        save = mPrefs.getBoolean(SAVED_SAVE, false);
        started = mPrefs.getBoolean(SAVED_STARTED, false);
        resumed = mPrefs.getBoolean(SAVED_RESUMED, false);

        setHasOptionsMenu(true);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelR = new RecordSensor();

//        registerSensors();
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
                addMarker();
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
                                        state = "bike";
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

        updateUX();

        mStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Recording
                EventLab.get(getActivity()).addEvent(mEvent);
                if (mClient.isConnected()) getLocation();
                mTracking.setStart(currentLocation);
                now.setOfficialSTime();
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
                active = false;
                save = true;
                resumed = false;
                updateTime();
                updateUX();
            }
        });

        mStatsRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mStatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStatsRecyclerView.setNestedScrollingEnabled(false);
        updateUI();

        now = (Time)mEvent.getStat(R.string.activity_item_time);
        mTracking = (LocationA)mEvent.getStat(R.string.activity_item_location);

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
                mDataTextView.setText(getString(R.string.time, now.getTimeMinutes(),
                        String.format(Locale.getDefault(), "%02d", now.getTimeSeconds()),
                        String.format(Locale.getDefault(), "%03d", now.getTimeMilli())));
            }

            if (stat instanceof LocationA){
                mDataTextView.setText("" + mTracking.getCurrent().getLatitude() + "°, " +
                        mTracking.getCurrent().getLongitude() + "°");
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
            mAdapter.setmStats(stats);
            mAdapter.notifyDataSetChanged();
        }

        if (mClient.isConnected()){
            getLocation();
        }

        updateEvent();
    }

    void updateUX(){
        getActivity().invalidateOptionsMenu();

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

    }

    void updateTime(){
        if( active && !save && started && !resumed){
            now.setOfficialSTime();
            now.setStartTime();
            if(mClient.isConnected()) getLocation();
            timer.postDelayed(runnable, 0);
            accelR.setLastUpdate(now.getCurrentTime());
        }

        if( active && !save && started && resumed){
            now.addDeltaTime();
            now.setStartTime();
            timer.postDelayed(runnable, 0);
            accelR.setLastUpdate(now.getCurrentTime());
        }

        if ( !active & !save && started){
            now.setEndTime();
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

    private void updatePhotoView(ImageView photoView, int idx) {
        File photoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, idx);
        if (photoView != null) {
            if (photoFile == null || !photoFile.exists()) {
                photoView.setImageDrawable(null);
            } else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                photoView.setImageBitmap(bitmap);
            }
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

            mMap.clear();
            ArrayList<Location> m = mTracking.getMarkers();
            ArrayList<String> ms = mTracking.getMarkerTitles();
//            ArrayList<String> mi = mEvent.getMarkers();

            for (int i = 0; i < m.size(); i++) {
                File photoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, i);
                showMarker(m.get(i).getLatitude(), m.get(i).getLongitude(), ms.get(i), photoFile);
            }

            FragmentManager manager = getFragmentManager();
            ShowLargePictureFragment dialog = ShowLargePictureFragment
                    .newInstance(mEvent.getPhotoFilename(marker_photo_idx-1));

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
                    addMarker();
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
            if(mClient.isConnected()) getLocation();
            if (currentLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(currentLocation.getLatitude(),
                        currentLocation.getLongitude()), 14.0f));
            }
        } catch (SecurityException xe) {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    public void addMarker(){
        mEvent.addPhotoFilename(marker_photo_idx);
        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, marker_photo_idx);
        //System.out.println(currentLocation);

        if (currentLocation != null && mPhotoFile != null) {
            // Open Dialog to take picture;
            long millis = now.getCurrentTime();
            mTracking.addMarkers(currentLocation, String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            );
//            mEvent.addPhotoFilename(marker_photo_idx);
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

    public void getLocation(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        // Android Studio would not let me compile without adding this try-catch block
        try {
//            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i(TAG, "Got a fix: " + location);
                            currentLocation = location;
                        }
                    });
        }catch (SecurityException xe){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }


    // Google Maps API Marker Documentation
    // https://developers.google.com/maps/documentation/android-api/marker
    public void showMarker(Double lat, Double lon, String title, File imageFile) {
        mMap.clear();
        // Create a LatLng object with the given Latitude and Longitude
        LatLng markerLoc = new LatLng(lat, lon);
        Bitmap bitmap = PictureUtils.getScaledBitmap(imageFile.getPath(), 200,200);

        //Add marker to map
        mMap.addMarker(new MarkerOptions()
                .position(markerLoc)                  // at the location you needed
                .title(title)                         // with a title you needed
                .snippet("")     // and also give some summary of available
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        ); // and give your animation drawable as icon
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

        updateEvent();
        mSensorManager.unregisterListener(this);

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean(SAVED_ACTIVE, active);
        ed.putBoolean(SAVED_SAVE, save);
        ed.putBoolean(SAVED_STARTED, started);
        ed.putBoolean(SAVED_RESUMED, resumed);
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

// Reference to draw path on map
// https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/