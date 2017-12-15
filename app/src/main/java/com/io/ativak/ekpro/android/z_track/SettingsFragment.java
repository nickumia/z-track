package com.io.ativak.ekpro.android.z_track;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by nick on 11/16/2017.
 *
 */

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    Switch mUnitsSwitch;
    CoordinatorLayout mWeightChange;
    TextView mWeightTextView;

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mUnitsSwitch = (Switch) v.findViewById(R.id.unitsSwitch);
        mWeightChange = (CoordinatorLayout) v.findViewById(R.id.weightChangeView);
        mWeightTextView = (TextView) v.findViewById(R.id.weightTextView);

        mUnitsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    MainActivity.UNIT = "SI";
                } else {
                    MainActivity.UNIT = "EN";
                }
            }
        });

        mWeightChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Weight Dialog
            }
        });

        if (MainActivity.UNIT.equals("SI")){
            mUnitsSwitch.setChecked(true);
        } else {
            mUnitsSwitch.setChecked(false);
        }

        return v;
    }
}
