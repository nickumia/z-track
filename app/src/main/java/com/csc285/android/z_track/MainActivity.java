package com.csc285.android.z_track;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String A_TAG = "ActivityFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            fm.replace(R.id.fragment_container, ActivityFragment.newInstance());
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
}
