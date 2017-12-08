package com.csc285.android.z_track;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        new ConfigurationTask().execute();
    }

    private class ConfigurationTask extends AsyncTask<Void,Void,Boolean> {
        private static final String TAG = "Configuration";
        private boolean mHasQuit = false;
        private Handler mRequestHandler;

        @Override
        protected Boolean doInBackground(Void... params) {

            SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

            for (Sensor sensor : deviceSensors) {
                System.out.println(TAG + sensor);
            }
//            // Here, thisActivity is the current activity
//            if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                    Manifest.permission.READ_CONTACTS)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                // Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(getApplicationContext(),
//                        Manifest.permission.READ_CONTACTS)) {
//
//                    // Show an explanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//
//                } else {
//
//                    // No explanation needed, we can request the permission.
//
//                    ActivityCompat.requestPermissions(getApplicationContext(),
//                            new String[]{Manifest.permission.READ_CONTACTS},
//                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                }
//            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            finish();
            Intent a = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(a);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }

            // other 'case' lines to check for other
            // permissions this app might request
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
