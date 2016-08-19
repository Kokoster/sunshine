package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_icon);
        imageView.setImageResource(R.mipmap.ic_launcher);

        String date = Utility.getFriendlyDayString(context,
                cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_date_textview);
        dateTextView.setText(date);

        String weatherForecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView weatherForecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        weatherForecastView.setText(weatherForecast);

        boolean isMetric = Utility.isMetric(context);

        double highTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highTextView = (TextView) view.findViewById(R.id.list_item_high_textview);
        highTextView.setText(Utility.formatTemperature(highTemp, isMetric));

        double lowTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowTextView = (TextView) view.findViewById(R.id.list_item_low_textview);
        lowTextView.setText(Utility.formatTemperature(lowTemp, isMetric));
    }
}