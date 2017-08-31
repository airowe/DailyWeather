package com.rowe.adam.switchfourweather.Views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rowe.adam.switchfourweather.Models.Forecast;
import com.rowe.adam.switchfourweather.Models.WeatherStatus;
import com.rowe.adam.switchfourweather.Presenters.ExtendedForecastAdapter;
import com.rowe.adam.switchfourweather.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * ExtendedForecastFragment
 * Fragment to show 7-Day Weather Outlook
 * @author Adam Rowe
 * @version 1.0
 */
public class ExtendedForecastFragment extends Fragment {

    private static final String weatherForecastUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final int numDaysForecast = 7;
    String apiParams;
    String city;
    List<Forecast> dailyWeather;
    //WeatherStatus weatherStatus;
    RecyclerView weatherForecast;
    Button toggleDisplayBtn;
    ExtendedForecastAdapter forecastAdapter;
    RecyclerViewClickListener listener;

    List<String> days = new ArrayList<>();

    public ExtendedForecastFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // weatherStatus = getArguments().getParcelable("dailyWeather");
            apiParams = getArguments().getString("apiParams");
            city = getArguments().getString("city");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_extended_forecast, container, false);

        weatherForecast = rootView.findViewById(R.id.daily_forecast);
        toggleDisplayBtn = rootView.findViewById(R.id.toggle_display_btn);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(weatherForecast.getContext(),
                LinearLayoutManager.VERTICAL);
        weatherForecast.addItemDecoration(dividerItemDecoration);

        getWeekDays();
        setUpClickListener();
        setUpAdapter();
        checkWeather();

        return rootView;
    }

    /**
     * Generates 2-character day strings
     */
    private void getWeekDays()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
        for (int i = 0; i < numDaysForecast; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            days.add(sdf.format(calendar.getTime()));
        }
    }

    /**
     * Sets up RecyclerView Adapter
     * Injects empty dailyWeather forecast data in to adapter
     * Fixed Size attribute for performance
     * LinearLayoutManager to set orientation (vertical)
     */
    private void setUpAdapter()
    {
        dailyWeather = new ArrayList<>();
        forecastAdapter = new ExtendedForecastAdapter(listener, dailyWeather, days, R.layout.weather_item);
        weatherForecast.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        weatherForecast.setLayoutManager(llm);
        weatherForecast.setAdapter(forecastAdapter);
    }

    private void setUpClickListener()
    {
        listener = (position) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("dailyWeather",dailyWeather.get(position));
            bundle.putString("city",city);
            bundle.putString("apiParams",apiParams);
            bundle.putInt("position",position);


            CurrentWeatherFragment currentWeatherFragment = new CurrentWeatherFragment();
            currentWeatherFragment.setArguments(bundle);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,currentWeatherFragment,"currentWeather")
                    .addToBackStack("currentWeather")
                    .commit();
        };
    }

    public interface RecyclerViewClickListener {
        void onClick(int position);
    }

    /**
     * Retrieves 7-day weather data from Open Weather Map API (JSON)
     */
    public void checkWeather() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String connectUrl = weatherForecastUrl + apiParams;
        httpClient.get(connectUrl, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                parseJson(response);
            }

            @Override
            public void onFailure(int errorCode, Header[] headers, Throwable exc, JSONObject response) {
                Log.e("FailureResponse",response.toString());
                Snackbar mySnackbar = Snackbar.make(weatherForecast,
                        "Problem downloading weather", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });
    }

    /**
     * Parse JSON from server using GSON's implicit parser
     * Notifies adapter of data changes to refresh UI
     * @param jsonResponse - JSONObject representing weather data
     */
    public void parseJson(JSONObject jsonResponse)
    {
        WeatherStatus weatherStatus = new Gson().fromJson(jsonResponse.toString(),WeatherStatus.class);
        dailyWeather = weatherStatus.forecast;
        forecastAdapter.setItems(dailyWeather);
        forecastAdapter.notifyDataSetChanged();
    }
}
