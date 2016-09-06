package com.example.android.sunshine.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

public class ForecastFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final int FORECAST_LOADER_ID = 0 ;
    private static final String LIST_ITEM_SELECTED_KEY = "selected_position";

    private static final String[] FORECAST_COLUMNS = {
        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
        WeatherContract.WeatherEntry.COLUMN_DATE,
        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
        WeatherContract.LocationEntry.COLUMN_COORD_LAT,
        WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private ForecastAdapter mForecastAdapter;
    private View mRootView;

    private ListView mForecastListView;
    private int mCurrentPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        mForecastListView = (ListView) mRootView.findViewById(R.id.listview_forecast);
        mForecastListView.setAdapter(mForecastAdapter);

        mForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    String locationSettings = Utility.getPreferredLocation(getActivity());

                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSettings, cursor.getLong(COL_WEATHER_DATE)
                            ));
                }
                mCurrentPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_ITEM_SELECTED_KEY)) {
            mCurrentPosition = savedInstanceState.getInt(LIST_ITEM_SELECTED_KEY);
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPosition != ListView.INVALID_POSITION) {
            outState.putInt(LIST_ITEM_SELECTED_KEY, mCurrentPosition);
        }

        super.onSaveInstanceState(outState);
    }

    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSettings = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSettings, System.currentTimeMillis());

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);

        if (mCurrentPosition != ListView.INVALID_POSITION) {
            mForecastListView.smoothScrollToPosition(mCurrentPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;

        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    private void openPreferredLocationInMap() {
        if (mForecastAdapter == null) {
            return;
        }

        Cursor cursor = mForecastAdapter.getCursor();
        if (cursor == null) {
            return;
        }

        cursor.moveToPosition(0);
        String posLat = cursor.getString(COL_COORD_LAT);
        String posLong = cursor.getString(COL_COORD_LONG);

        Log.d(LOG_TAG, "long: " + posLong + " lat: " + posLat);

        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }
}
