package com.csc285.android.z_track;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";
    private TextView mAppTextView;
    private TextView mBuildTextView;

    public static AboutFragment newInstance()
    {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        mAppTextView = (TextView) v.findViewById(R.id.appName);
        mBuildTextView = (TextView) v.findViewById(R.id.buildTextView);

        mBuildTextView.setText("" + getString(R.string.app_build) + " "+ Build.VERSION.SDK_INT);
        return v;
    }
}
