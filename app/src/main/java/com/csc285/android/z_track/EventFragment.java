package com.csc285.android.z_track;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

/**
 * Fragment to view Events in the History View
 */

public class EventFragment extends Fragment {

    public static final String ARG_EVENT_ID = "event_id";
    Event mEvent;
//    private Callbacks mCallbacks;

    TextView eventNameTextView;

    public static EventFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, crimeId);
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID eventId = (UUID) getArguments().getSerializable(ARG_EVENT_ID);
        mEvent = EventLab.get(getActivity()).getEvent(eventId);

        // TODO: WHY DOES SYSTEM.OUT PRINT!!!!!!
        if (mEvent == null){
            getActivity().finish();
            startActivity(getActivity().getIntent());
            System.out.println("WHATZ@DFSDF");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, parent, false);

        eventNameTextView = (TextView) v.findViewById(R.id.event_name);
        eventNameTextView.setText("" + "" + mEvent.getmDate().toString());

        return v;
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
    public void onAttach(Context context) {
        super.onAttach(context);
//        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mCallbacks = null;
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
