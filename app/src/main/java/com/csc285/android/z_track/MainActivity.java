package com.csc285.android.z_track;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String A_TAG = "ActivityFragment";
    private static final String EXTRA_EVENT_ID = "com.csc285.android.z_track.event_id";
    private static final String SAVED_UNIT = "units";
    public static String UNIT = "SI";

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences("App Pref", 0);
        UNIT = mPrefs.getString(SAVED_UNIT, "SI");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        ActivityFragment act = new ActivityFragment();
        fm.replace(R.id.fragment_container, act);
        fm.addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_record) {
            Event m = new Event();
            fm.replace(R.id.fragment_container, ActivityFragment.newInstance(m.getmId()));
            fm.addToBackStack(null).commit();
        } else if (id == R.id.nav_history) {
            fm.replace(R.id.fragment_container, HistoryFragment.newInstance());
            fm.addToBackStack(null).commit();
        } else if (id == R.id.nav_search) {
            fm.replace(R.id.fragment_container, SearchFragment.newInstance());
            fm.addToBackStack(null).commit();
        } else if (id == R.id.nav_about) {
            fm.replace(R.id.fragment_container, AboutFragment.newInstance());
            fm.addToBackStack(null).commit();
        } else if (id == R.id.nav_settings) {
            fm.replace(R.id.fragment_container, SettingsFragment.newInstance());
            fm.addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString(SAVED_UNIT, UNIT);
        ed.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        getSupportActionBar().setTitle(R.string.app_name);
    }

    public void startHistoryFragment(View v){
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.fragment_container, HistoryFragment.newInstance());
        fm.addToBackStack(null).commit();
    }
}
