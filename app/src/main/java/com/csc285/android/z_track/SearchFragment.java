package com.csc285.android.z_track;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csc285.android.z_track.Statistics.Distance;
import com.csc285.android.z_track.Statistics.LocationA;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class SearchFragment extends Fragment implements com.google.android.gms.location.LocationListener {

    private static final String TAG = "ActivityFragment";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private RecyclerView mSearchRecyclerView;
    private SearchAdapter mAdapter;
    private Button mManual, mAuto, mDist1, mDist2, mDist3;
    private TextView mNone;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location currentLocation;

    private List<Event> shared;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public static SearchFragment newInstance()
    {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
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

        try {
            // Solved Callback problem
            // https://stackoverflow.com/questions/9007600/onlocationchanged-callback-is-never-called
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException se){
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            findLocationRoutes(0);
            Toast.makeText(getActivity(),"Updating..",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchRecyclerView = (RecyclerView) v.findViewById(R.id.search_recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchRecyclerView.setNestedScrollingEnabled(false);

        mManual = (Button) v.findViewById(R.id.manual);
        mAuto = (Button) v.findViewById(R.id.auto);
        mDist1 = (Button) v.findViewById(R.id.ten);
        mDist2 = (Button) v.findViewById(R.id.fiften);
        mDist3 = (Button) v.findViewById(R.id.twentyfive);
        mNone = (TextView) v.findViewById(R.id.noRoutes);

        mManual.setEnabled(false);

        mManual.setBackground(getResources().getDrawable(R.drawable.not_selected));
        mAuto.setBackground(getResources().getDrawable(R.drawable.selected));

        mDist1.setBackground(getResources().getDrawable(R.drawable.not_selected));
        mDist2.setBackground(getResources().getDrawable(R.drawable.selected));
        mDist3.setBackground(getResources().getDrawable(R.drawable.not_selected));

        mAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManual.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mAuto.setBackground(getResources().getDrawable(R.drawable.selected));
                findLocationRoutes(0);
            }
        });

        mDist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDist1.setBackground(getResources().getDrawable(R.drawable.selected));
                mDist2.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mDist3.setBackground(getResources().getDrawable(R.drawable.not_selected));
                findLocationRoutes(1);
            }
        });

        mDist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDist1.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mDist2.setBackground(getResources().getDrawable(R.drawable.selected));
                mDist3.setBackground(getResources().getDrawable(R.drawable.not_selected));
                findLocationRoutes(2);
            }
        });

        mDist3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDist1.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mDist2.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mDist3.setBackground(getResources().getDrawable(R.drawable.selected));
                findLocationRoutes(3);
            }
        });

        findLocationRoutes(0);
        updateUI();

        return v;
    }

    private class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mLengthTextView;
        private TextView mRatingTextView;
        private TextView mDistanceTextView;
        private Event mEvent;

        SearchHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_search_item, parent, false));

            mLengthTextView = (TextView) itemView.findViewById(R.id.distance);
            mRatingTextView = (TextView) itemView.findViewById(R.id.rating);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.distanceAway);
        }

        /** Bind
         * @param event
         *
         * String Array Reference Page
         * https://developer.android.com/guide/topics/resources/string-resource.html#StringArray
         *
         */
        void bind(Event event) {
            mEvent = event;

            if (MainActivity.UNIT.equals("SI")) {
                mLengthTextView.setText("" +
                        String.format(Locale.getDefault(), "%.4f",
                                ((Distance) mEvent.getStat(R.string.activity_item_distance)).getTotalDistance())
                + " km");
            } else {
                mLengthTextView.setText("" + "" +
                        String.format(Locale.getDefault(), "%.4f",
                            ((Distance) mEvent.getStat(R.string.activity_item_distance))
                                .kmToMi(((Distance) mEvent.getStat(R.string.activity_item_distance))
                                .getTotalDistance()))
                        + " mi");
            }

            mRatingTextView.setText("" + mEvent.getRating() + " Stars");
        }

        @Override
        public void onClick(View view) {
//            mCallbacks.onEventSelected(mEvent);
//            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
//            fm.replace(R.id.fragment_container, EventPagerFragment.newInstance(mEvent.getmId()));
//            fm.addToBackStack(null).commit();
//            Intent ni = EventPagerFragment.newIntent(getContext(), mEvent.getmId());
////            Intent ni = new Intent(getContext(), EventPagerFragment.class);
//            startActivity(ni);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
        private List<Event> mEvent;

        SearchAdapter(List<Event> stat) {
            mEvent = stat;

//            double seconds = 0;
//            double dist = 0;
//            ltNum = mEvent.size();
//            for(int i = 0; i < ltNum; i++){
//                Time t = ((Time) mEvent.get(i).getStat(R.string.activity_item_time));
//                seconds += t.getOfficialTimeS() + (t.getOfficialTimeM()*60) + (t.getOfficialTimeMS()/1000);
//                dist += ((Distance) mEvent.get(i).getStat(R.string.activity_item_distance)).getTotalDistance();
//            }
//
//            ltDistance = dist;
//            ltHours = seconds/3600;

        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new SearchHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            Event stat = mEvent.get(position);
            holder.bind(stat);
        }

        @Override
        public int getItemCount() {
            return mEvent.size();
        }

        void setmEvent(List<Event> stats) { mEvent = stats; }
    }

    private void updateUI() {
        List<Event> events = shared;

        if (shared.size() != 0) {
            mNone.setVisibility(View.GONE);
            if (mSearchRecyclerView != null) {
                if (mAdapter == null) {
                    mAdapter = new SearchAdapter(events);
                    mSearchRecyclerView.setAdapter(mAdapter);
                } else {
                    mSearchRecyclerView.setAdapter(mAdapter);
                    mAdapter.setmEvent(events);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            mNone.setVisibility(View.VISIBLE);
        }

    }

    void findLocationRoutes(int i){
        shared = EventLab.get(getActivity()).getSharedRoutes();

        Distance d = new Distance();
        LatLng a,b;

        for(Event e: shared){
            a = new LatLng(((LocationA)e.getStat(R.string.activity_item_location)).getStart().getLatitude(),
                    ((LocationA)e.getStat(R.string.activity_item_location)).getStart().getLongitude());
            b = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            if (d.getDistanceFromLatLonInKm(a,b) > d.kmToMi(25)){
                shared.remove(e);
            }
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        findLocationRoutes(0);
    }
}
