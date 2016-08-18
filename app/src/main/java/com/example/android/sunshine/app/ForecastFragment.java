package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> mForecastAdapter;
    private View mRootView;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);


        mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, new ArrayList<String>());

        ListView forecastList = (ListView) mRootView.findViewById(R.id.listview_forecast);
        forecastList.setAdapter(mForecastAdapter);

        forecastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                updateWeather();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cityIDPreference = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        String temperatureUnitsPreference = preferences.getString(getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric));

        fetchWeatherTask.execute(cityIDPreference, temperatureUnitsPreference);
    }
}
