package com.io.ativak.ekpro.android.z_track;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.io.ativak.ekpro.android.z_track.Statistics.Distance;
import com.io.ativak.ekpro.android.z_track.Statistics.LocationA;
import com.io.ativak.ekpro.android.z_track.Statistics.Rating;
import com.io.ativak.ekpro.android.z_track.Statistics.Statistics;
import com.io.ativak.ekpro.android.z_track.Statistics.Time;
import com.io.ativak.ekpro.android.z_track.Statistics.Visit;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class ReviewFragment extends Fragment {

    private static final String TAG = "ReviewFragment";
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mAdapter;
    private Event current;
    private Button mSubmitRoute;

    public static ReviewFragment newInstance()
    {
        return new ReviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = EventLab.get(getActivity()).getSharingEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review, container, false);

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.review_recycler_view);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewRecyclerView.setNestedScrollingEnabled(false);

        mSubmitRoute = (Button) v.findViewById(R.id.submitRoute);
        mSubmitRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventLab.get(getActivity()).addSharedRoute(current);

                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, SearchFragment.newInstance());
                fm.addToBackStack(null).commit();
            }
        });

        updateUI();

        return v;
    }

    private class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;
        private TextView mData;
        private Statistics mStat;

        ReviewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_post_item, parent, false));

            mTitle = (TextView) itemView.findViewById(R.id.review_title);
            mData = (TextView) itemView.findViewById(R.id.review_data);
        }

        /** Bind
         * @param stat
         *
         * String Array Reference Page
         * https://developer.android.com/guide/topics/resources/string-resource.html#StringArray
         *
         */
        void bind(Statistics stat) {
            mStat = stat;

            mTitle.setText("" + getResources().getString(stat.getId()) + ":");

            if (stat instanceof Time) {
                if (stat.getId() == R.string.activity_item_times) {
                    mData.setText(new Date(((Time) stat).getOfficialSTime()).toString());
                }
                if (stat.getId() == R.string.activity_item_time) {
                    mData.setText(getString(R.string.time, ((Time)stat).getTimeMinutes(),
                            String.format(Locale.getDefault(), "%02d", ((Time)stat).getTimeSeconds()),
                            String.format(Locale.getDefault(), "%03d", ((Time)stat).getTimeMilli())));
                }
            }

            if (stat instanceof Distance) {
                mData.setText("" + "" +
                        String.format(Locale.getDefault(), "%.4f", ((Distance)stat).getTotalDistance()));
            }

            if (stat instanceof LocationA) {
                if (stat.getId() == R.string.activity_item_location) {
                    mData.setText("" +
                            String.format(Locale.getDefault(), "%.2f", ((LocationA)stat).getStart().getLatitude()) + "°, " +
                            String.format(Locale.getDefault(), "%.2f", ((LocationA)stat).getStart().getLongitude()) + "°");
                }
            }

            if (stat instanceof Visit) {
                if (stat.getId() == R.string.visit_title) {
                    mData.setText("" + "" + ((Visit)stat).getVisited());
                }
            }

            if (stat instanceof Rating) {
                if (stat.getId() == R.string.rating_title) {
                    mData.setText("" + "" +
                            String.format(Locale.getDefault(), "%.1f", ((Rating)stat).getRating())
                            );
                }
            }
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

    private class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {
        private List<Statistics> mReview;

        ReviewAdapter(List<Statistics> stat) {
            mReview = stat;

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
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ReviewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ReviewHolder holder, int position) {
            Statistics stat = mReview.get(position);
            holder.bind(stat);
        }

        @Override
        public int getItemCount() {
            return mReview.size();
        }

        void setmEvent(List<Statistics> stats) { mReview = stats; }
    }

    private void updateUI() {
        EventLab eventLab = EventLab.get(getActivity());
        List<Statistics> events = eventLab.getSharingEvent().getmReview();

        if (mReviewRecyclerView != null) {
            if (mAdapter == null) {
                mAdapter = new ReviewAdapter(events);
                mReviewRecyclerView.setAdapter(mAdapter);
            } else {
                mReviewRecyclerView.setAdapter(mAdapter);
                mAdapter.setmEvent(events);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    void findLocationRoutes(int i){

    }
}
