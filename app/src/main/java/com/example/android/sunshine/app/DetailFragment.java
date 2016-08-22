package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by kokoster on 21.08.16.
 */

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int DETAIL_LOADER = 0;

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String mForecast;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_WIND_DIRECTION = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider mShareActionProvider;

    private ImageView mForecastImageView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mWeatherDescView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mForecastImageView = (ImageView) rootView.findViewById(R.id.icon);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.friendly_date_textview);
        mDateView = (TextView) rootView.findViewById(R.id.date_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.high_temp_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.low_temp_textview);
        mWeatherDescView = (TextView) rootView.findViewById(R.id.forecast_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.pressure_textview);

        return rootView;
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        updateDetailView();
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + " " + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }

        Uri dataUri = intent.getData();
        return new CursorLoader(
                getActivity(),
                dataUri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mPressureView

        if (data == null || !data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

        mForecastImageView.setImageResource(R.mipmap.ic_launcher);

        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mDateView.setText(dateText);

        boolean isMetric = Utility.isMetric(getActivity());

        String highTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighTempView.setText(highTemp);
        String lowTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowTempView.setText(lowTemp);

        String weatherDesc = data.getString(COL_WEATHER_DESC);
        mWeatherDescView.setText(weatherDesc);

        String humidity = String.format(getString(R.string.format_humidity),
                data.getDouble(COL_WEATHER_HUMIDITY));
        mHumidityView.setText(humidity);


        String windData = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED),
                data.getFloat(COL_WEATHER_WIND_DIRECTION));
        mWindView.setText(windData);

        String pressure = String.format(getString(R.string.format_pressure),
                data.getDouble(COL_WEATHER_PRESSURE));
        mPressureView.setText(pressure);

        mForecast = String.format("%s - %s - %s/%s", dateText, weatherDesc, highTemp, lowTemp);

        updateDetailView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void updateDetailView() {
        if (mForecastImageView == null || mDateView == null ||
            mHighTempView == null || mLowTempView == null ||
            mWeatherDescView == null || mHumidityView == null ||
            mWindView == null || mPressureView == null ||
            mShareActionProvider == null) {

            return;
        }

        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }
}
