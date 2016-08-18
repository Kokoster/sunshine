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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(APP_LOG_TAG, "in onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(APP_LOG_TAG, "in onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(APP_LOG_TAG, "in onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(APP_LOG_TAG, "in onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(APP_LOG_TAG, "in onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(APP_LOG_TAG, "in onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(APP_LOG_TAG, "in onRestart");
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

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
