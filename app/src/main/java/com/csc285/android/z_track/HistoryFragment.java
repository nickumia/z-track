package com.csc285.android.z_track;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class HistoryFragment extends Fragment {

    private static final String TAG = "ActivityFragment";

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

        return v;
    }
}
