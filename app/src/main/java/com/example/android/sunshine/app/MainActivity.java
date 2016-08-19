package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String APP_LOG_TAG = "android.sunshine.app";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FORECASTFRAGMENT_TAG = "forecast_fragment";

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }

        mLocation = Utility.getPreferredLocation(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentLocation = Utility.getPreferredLocation(this);

        if (currentLocation != null && !currentLocation.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentByTag(FORECASTFRAGMENT_TAG);


            if (ff != null) {
                ff.onLocationChanged();
            }

            mLocation = currentLocation;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));

                return true;

            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?q=").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geoLocation);

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Log.e(LOG_TAG, "Couldn't call " + location + ", no app");
        }
    }
}
