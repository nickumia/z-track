package com.io.ativak.ekpro.android.z_track;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.io.ativak.ekpro.android.z_track.Statistics.Distance;
import com.io.ativak.ekpro.android.z_track.Statistics.Time;

import java.util.List;
import java.util.Locale;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private RecyclerView mEventRecyclerView;
    private EventAdapter mAdapter;
    private TextView lifetimeStats;
//    private Callbacks mCallbacks;

    private double ltHours;
    private double ltDistance;
    private int ltNum;

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

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                EventLab eventLab = EventLab.get(getActivity());
                List<Event> events = eventLab.getEvents();
                EventLab.get(getContext()).delEvent(events.get(viewHolder.getAdapterPosition()));
                updateUI();
            }
        };

        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackAlt = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                EventLab eventLab = EventLab.get(getActivity());
                List<Event> events = eventLab.getEvents();
                EventLab.get(getContext()).delEvent(events.get(viewHolder.getAdapterPosition()));
                updateUI();
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mEventRecyclerView);
        new ItemTouchHelper(itemTouchHelperCallbackAlt).attachToRecyclerView(mEventRecyclerView);

        lifetimeStats = (TextView) v.findViewById(R.id.lifetimeStats);
        updateUI();

        Toast.makeText(getActivity(), R.string.deleteEvent, Toast.LENGTH_SHORT).show();

        return v;
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private Event mEvent;
        private ImageView mTypeView;

        EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_history_item, parent, false));

            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.event_title);
            mTypeView = (ImageView) itemView.findViewById(R.id.activity_type);

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
            String datetime = event.getmDate().toString();
            mNameTextView.setText(
                    //mEvent.getAcType().toUpperCase() + " on " +
                    datetime.substring(0,10) +
                    " at " + datetime.substring(11,16));

            if (mEvent.getAcType().equals("walk")) {
                mTypeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_walk));
            } else if (mEvent.getAcType().equals("bike")) {
                mTypeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_bike));
            } else {
                mTypeView.setImageDrawable(getResources().getDrawable(R.drawable.ic_other));
            }
        }

        @Override
        public void onClick(View view) {
//            mCallbacks.onEventSelected(mEvent);
            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.fragment_container, EventPagerFragment.newInstance(mEvent.getmId()));
            fm.addToBackStack(null).commit();
//            Intent ni = EventPagerFragment.newIntent(getContext(), mEvent.getmId());
////            Intent ni = new Intent(getContext(), EventPagerFragment.class);
//            startActivity(ni);
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {
        private List<Event> mEvent;

        EventAdapter(List<Event> stat) {
            mEvent = stat;

            double seconds = 0;
            double dist = 0;
            ltNum = mEvent.size();
            for(int i = 0; i < ltNum; i++){
                Time t = ((Time) mEvent.get(i).getStat(R.string.activity_item_time));
                seconds += t.getOfficialTimeS() + (t.getOfficialTimeM()*60) + (t.getOfficialTimeMS()/1000);
                dist += ((Distance) mEvent.get(i).getStat(R.string.activity_item_distance)).getTotalDistance();
            }

            ltDistance = dist;
            ltHours = seconds/3600;

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

        if (mEventRecyclerView != null && lifetimeStats !=null) {
            if (mAdapter == null) {
                mAdapter = new EventAdapter(events);
                mEventRecyclerView.setAdapter(mAdapter);
            } else {
                mEventRecyclerView.setAdapter(mAdapter);
                mAdapter.setmEvent(events);
                mAdapter.notifyDataSetChanged();
            }

            if (MainActivity.UNIT.equals("SI")) {
                lifetimeStats.setText(getString(R.string.lifetimeStatsSI,
                        String.format(Locale.getDefault(), "%01f", ltHours),
                        String.format(Locale.getDefault(), "%01f", ltDistance),
                        String.format(Locale.getDefault(), "%d", ltNum)));
            } else {
                lifetimeStats.setText(getString(R.string.lifetimeStatsEN,
                        String.format(Locale.getDefault(), "%01f", ltHours),
                        String.format(Locale.getDefault(), "%01f", ltDistance),
                        String.format(Locale.getDefault(), "%d", ltNum)));
            }
        }
    }

    interface Callbacks {
        void onEventSelected(Event event);
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
}
