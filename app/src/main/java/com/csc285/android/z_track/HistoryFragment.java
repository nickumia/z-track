package com.csc285.android.z_track;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private RecyclerView mEventRecyclerView;
    private EventAdapter mAdapter;
    private Event mEvent;

    public static HistoryFragment newInstance()
    {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.stats_recycler_view);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventRecyclerView.setNestedScrollingEnabled(false);
        updateUI();

        return v;
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private Event mEvent;

        EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_stats_item, parent, false));

            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.event_title);


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
            mNameTextView.setText(event.getmDate().toString());
        }

        @Override
        public void onClick(View view) {
//            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<Event> mEvent;

        EventAdapter(List<Event> stat) {
            mEvent = stat;
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
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
        EventLab eventLab = EventLab.get(getActivity());
        List<Event> events = eventLab.getEvents();

        if (mAdapter == null) {
            mAdapter = new EventAdapter(events);
            mEventRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmEvent(events);
            mAdapter.notifyDataSetChanged();
        }
    }
}
