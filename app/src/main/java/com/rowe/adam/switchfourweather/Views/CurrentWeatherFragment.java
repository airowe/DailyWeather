package com.rowe.adam.switchfourweather.Views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rowe.adam.switchfourweather.Models.Forecast;
import com.rowe.adam.switchfourweather.Models.Weather;
import com.rowe.adam.switchfourweather.R;

import java.util.Locale;

/**
 * CurrentWeatherFragment
 * Fragment to show today's Weather Outlook
 * @author Adam Rowe
 * @version 1.0
 */
public class CurrentWeatherFragment extends Fragment {

    String apiParams = "";
    String city = "";
    Weather weather;
    Forecast dailyWeather;
    int position;

    TextView cityField;
    TextView weatherStatusField;
    TextView detailsField;
    TextView currentTemperatureField;
    ImageView weatherIcon;

    public CurrentWeatherFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dailyWeather = getArguments().getParcelable("dailyWeather");
            city = getArguments().getString("city");
            apiParams = getArguments().getString("apiParams");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_weather, container, false);
        cityField = rootView.findViewById(R.id.city_field);
        weatherStatusField = rootView.findViewById(R.id.weather_status_field);
        detailsField = rootView.findViewById(R.id.details_field);

        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        displayWeatherInfo();

        return rootView;
    }

    /**
     * Parse weather data passed in from parent Activity
     * Render data to UI's Text and Image Views
     */
    private void displayWeatherInfo()
    {
        if(dailyWeather != null && dailyWeather.weather != null && dailyWeather.weather.size() > 0)
            weather = dailyWeather.weather.get(0);

        setDetails();
        setIcon();
        setCurrentTemp();
    }

    /**
     * Renders current temperature in formatted string to TextView
     */
    private void setCurrentTemp()
    {
        String tempString = String.format(Locale.getDefault(),"%d", (int) dailyWeather.temp.day) + " °F";
        currentTemperatureField.setText(tempString);
    }

    /**
     * Retrieve weather icon from server using Glide's Image Library
     * Resizes and fits in dimensions of ImageView
     */
    private void setIcon()
    {
        Glide.with(this)
                .load(String.format("http://openweathermap.org/img/w/%s.png",weather.icon))
                .asBitmap()
                .override(300, 300)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        weatherIcon.setImageBitmap(resource);
                    }
                });
    }

    /**
     * Renders weather details in formatted strings to TextViews
     */
    private void setDetails()
    {
        cityField.setText(city.toUpperCase());
        weatherStatusField.setText(weather.description.toUpperCase(Locale.US));
        detailsField.setText(
                "Humidity: " + dailyWeather.humidity + "%" +
                        "\n" + "Pressure: " + dailyWeather.pressure + " hPa");
    }
}