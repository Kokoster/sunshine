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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {
        private final int DETAIL_LOADER = 0;

        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;

        private ShareActionProvider mShareActionProvider;
        private String mWeatherData;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            getActivity().getMenuInflater().inflate(R.menu.detailfragment, menu);

            MenuItem item = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            updateForecastTextView();
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mWeatherData + " " + FORECAST_SHARE_HASHTAG);

            return shareIntent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
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
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {
                return;
            }

            String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
            String weatherDesc = data.getString(COL_WEATHER_DESC);

            boolean isMetric = Utility.isMetric(getActivity());

            String highTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String lowTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            mWeatherData = String.format("%s - %s - %s/%s", dateString, weatherDesc, highTemp, lowTemp);

            TextView textView = (TextView) getView().findViewById(R.id.text_detail);
            textView.setText(mWeatherData);

            updateForecastTextView();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {}

        private void updateForecastTextView() {
            if (mWeatherData == null || mShareActionProvider == null) {
                return;
            }

            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }
}