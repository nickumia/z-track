package com.io.ativak.ekpro.android.z_track;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import com.io.ativak.ekpro.android.z_track.Statistics.Visit;

import java.util.UUID;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class PostFragment extends Fragment {

    private static final String TAG = "PostFragment";
    private static final String ARG_EVENT_ID = "event_id";
    Button mScenic;
    Button mChallenge;
    Button mReview;
    TextView mLabel;
    RatingBar mRating;
    CheckBox mMarkers;
    UUID eventID;
    Event sharing;

    boolean scenic;

    public static PostFragment newInstance(UUID eventId)
    {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId.toString());
        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = (UUID) UUID.fromString(getArguments().getString(ARG_EVENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        mScenic = (Button) v.findViewById(R.id.scenic);
        mChallenge = (Button) v.findViewById(R.id.challenge);
        mLabel = (TextView) v.findViewById(R.id.ratingTitle);
        mRating = (RatingBar) v.findViewById(R.id.ratingBar);
        mMarkers = (CheckBox) v.findViewById(R.id.addMarkers);
        mReview = (Button) v.findViewById(R.id.review);

        mLabel.setText("");
        mRating.setEnabled(false);
        mMarkers.setEnabled(false);
        mReview.setEnabled(false);

        mScenic.setBackground(getResources().getDrawable(R.drawable.not_selected));
        mChallenge.setBackground(getResources().getDrawable(R.drawable.not_selected));

        mChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scenic = false;
                mChallenge.setBackground(getResources().getDrawable(R.drawable.selected));
                mScenic.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mLabel.setText(getString(R.string.post2b));
                mRating.setEnabled(true);
                mMarkers.setEnabled(true);
                mReview.setEnabled(true);
            }
        });

        mScenic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scenic = true;
                mScenic.setBackground(getResources().getDrawable(R.drawable.selected));
                mChallenge.setBackground(getResources().getDrawable(R.drawable.not_selected));
                mLabel.setText(getString(R.string.post2));
                mRating.setEnabled(true);
                mMarkers.setEnabled(true);
                mReview.setEnabled(true);
            }
        });

        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = mRating.getRating();
                boolean shareMarker = mMarkers.isChecked();

                sharing = EventLab.get(getActivity()).getEvent(eventID);
                sharing.setmReview(
                        (sharing.getStat(R.string.activity_item_time)),
                        R.string.activity_item_time);
                sharing.setmReview(rating, R.string.rating_title);
                sharing.setmReview(
                        (sharing.getStat(R.string.activity_item_distance)),
                        R.string.activity_item_distance);

                Visit v = new Visit();
                v.setVisited(1);
                sharing.setmReview(v, R.string.visit_title);
//                sharing.setScenic(scenic);
                sharing.setShareMarkers(shareMarker);

                EventLab.get(getActivity()).setSharingEvent(sharing);

                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, ReviewFragment.newInstance());
                fm.addToBackStack(null).commit();

            }
        });

        return v;
    }
}
