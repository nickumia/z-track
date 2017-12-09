package com.csc285.android.z_track;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.Elevation;
import com.csc285.android.z_track.Statistics.LocationA;
import com.csc285.android.z_track.Statistics.Pace;
import com.csc285.android.z_track.Statistics.Statistics;
import com.csc285.android.z_track.Statistics.Time;
import com.csc285.android.z_track.Statistics.Velocity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

/**
 * Fragment to view Events in the History View
 */

public class EventFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    public static final String ARG_EVENT_ID = "event_id";
    public static final String ARG_STATE = "starting_activity";
    private static final String DIALOG_MARKER = "DialogMarker";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_TIME = 1;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private GoogleMap mMap;
    Event mEvent;
    Time now;
    LocationA mTracking;
    Distance mDistance;
    Elevation mElevation;
    Pace mPace;
    Velocity mVelocity, mVelocityAvg;
    Polyline path;

    private RecyclerView mStatsRecyclerView;
    private StatsAdapter mAdapter;
    boolean mBadStart;

    TextView eventNameTextView;

    public static EventFragment newInstance(UUID crimeId, boolean start) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, crimeId);
        args.putBoolean(ARG_STATE, start);
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID eventId = (UUID) getArguments().getSerializable(ARG_EVENT_ID);
        mBadStart = getArguments().getBoolean(ARG_STATE);
        mEvent = EventLab.get(getActivity()).getEvent(eventId);

        // TODO: WHY DOES SYSTEM.OUT PRINT!!!!!!
        if (mEvent == null){
            getActivity().finish();
            startActivity(getActivity().getIntent());
            System.out.println("WHATZ@DFSDF");
        }

        System.out.println(((Time) mEvent.getmStats().get(0)).getOfficialSTime());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v;
        if (mBadStart) {
            v = inflater.inflate(R.layout.fragment_event_tainted, parent, false);
        } else {
            v = inflater.inflate(R.layout.fragment_event, parent, false);
        }

        eventNameTextView = (TextView) v.findViewById(R.id.event_name);
        NestedScrollView mNSV = (NestedScrollView) v.findViewById(R.id.nsv);
        mStatsRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mStatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStatsRecyclerView.setNestedScrollingEnabled(false);

        now = (Time)mEvent.getStat(R.string.activity_item_time);
        mTracking = (LocationA)mEvent.getStat(R.string.activity_item_location);
        mDistance = (Distance)mEvent.getStat(R.string.activity_item_distance);
        mElevation = (Elevation)mEvent.getStat(R.string.activity_item_elevation);
        mPace = (Pace)mEvent.getStat(R.string.activity_item_pace);
        mVelocity = (Velocity)mEvent.getStat(R.string.activity_item_topspeed);
        mVelocityAvg = (Velocity)mEvent.getStat(R.string.activity_item_avgspeed);

        updateUI();
        eventNameTextView.setText("" + "" + mEvent.getmDate().toString());

        mNSV.fullScroll(NestedScrollView.FOCUS_UP);
        mNSV.scrollTo(0,mNSV.getTop());
        mNSV.smoothScrollTo(0,0);

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
                int time[] = now.getOfficialTime();
                mDataTextView.setText(getString(R.string.time, time[0],
                        String.format(Locale.getDefault(), "%02d", time[1]),
                        String.format(Locale.getDefault(), "%03d", time[2])));
            }

            if (stat instanceof Pace){
                mDataTextView.setText("" + "" + mPace.getPace());
            }

            if (stat instanceof Velocity){
                if (stat.getId() == R.string.activity_item_topspeed){
                    mDataTextView.setText("" + "" + mVelocity.getTopVelocity());
                } else {
                    mDataTextView.setText("" + "" + mVelocityAvg.getAvgVelocity());
                }
            }

            if (stat instanceof Distance){
                mDataTextView.setText("" + "" + mDistance.getTotalDistance());
            }

            if (stat instanceof Elevation){
                mDataTextView.setText("" + "" + mElevation.getElevation());
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

    class StatsAdapter extends RecyclerView.Adapter<StatsHolder> {
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

    private void updateUI() {
        List<Statistics> stats = mEvent.getmStats();

        if (mAdapter == null) {
            mAdapter = new StatsAdapter(stats);
            mStatsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmStats(stats);
            mAdapter.notifyDataSetChanged();
        }

        updateEvent();
        drawPath();
    }

    private void drawPath(){

        if (mMap != null) {
            mMap.clear();  //clears all Markers and Polylines

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int i = 0; i < mTracking.getPath().size(); i++) {
                LatLng point = mTracking.getPath().get(i);
                options.add(point);
            }
//            addMarkerImage(); //add Marker in current position
            path = mMap.addPolyline(options); //add Polyline

            ArrayList<Location> m = mTracking.getMarkers();
            ArrayList<String> ms = mTracking.getMarkerTitles();
//            ArrayList<String> mi = mEvent.getMarkers();

            for (int i = 1; i < m.size(); i++) {
                if (i < mEvent.getPhotoSize()) {
                    File photoFile = EventLab.get(getActivity()).getPhotoFile(mEvent, i);
                    showMarker(m.get(i).getLatitude(), m.get(i).getLongitude(), ms.get(i), photoFile);
                }
            }
        }
    }

    public void showMarker(Double lat, Double lon, String title, File imageFile) {
        if (imageFile.exists()) {
            mMap.clear();
            // Create a LatLng object with the given Latitude and Longitude
            LatLng markerLoc = new LatLng(lat, lon);

            //Add marker to map
            Bitmap bitmap = PictureUtils.getScaledBitmap(imageFile.getPath(), 200, 200);
            mMap.addMarker(new MarkerOptions()
                    .position(markerLoc)                  // at the location you needed
                    .title(title)                         // with a title you needed
                    .snippet("")     // and also give some summary of available
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            ); // and give your animation drawable as icon
        }
    }

    public void returnResult() {
        Intent id = getActivity().getIntent();
        id.putExtra(ARG_EVENT_ID, mEvent.getmDate());
        getActivity().setResult(Activity.RESULT_OK, id);
    }

    private void updateEvent() {
        EventLab.get(getActivity()).updateEvent(mEvent);
//        mCallbacks.onEventUpdated(mEvent);
    }

    public interface Callbacks {
        void onEventUpdated(Event event);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(mTracking.getStart().getLatitude(),
                    mTracking.getStart().getLongitude()), 14.0f));
            updateUI();
        } catch (SecurityException xe) {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        String title = marker.getTitle();
        ArrayList<String> ms = mTracking.getMarkerTitles();
        int idx = 1;

        for (int i = 1; i < ms.size(); i++){
            if (ms.get(i).equals(title)){
                idx = i;
                break;
            }
        }

        FragmentManager manager = getFragmentManager();
        ShowLargePictureFragment dialog = ShowLargePictureFragment
                .newInstance(mEvent.getPhotoFilename(idx), ms.get(idx));

        dialog.setTargetFragment(EventFragment.this, REQUEST_TIME);
        dialog.show(manager, DIALOG_MARKER);

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart(){
        super.onStart();
        returnResult();
    }

    @Override
    public void onResume(){
        super.onResume();
        returnResult();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventLab.get(getActivity()).updateEvent(mEvent);
    }

}
