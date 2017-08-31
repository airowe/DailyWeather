package com.rowe.adam.switchfourweather.Views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rowe.adam.switchfourweather.Models.WeatherStatus;
import com.rowe.adam.switchfourweather.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * WeatherActivity
 * Parent View and entry point of application
 * @author Adam Rowe
 * @version 1.0
 */
public class WeatherActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?";
    private String latAndLongString = "lat=%f&lon=%f";
    private String apiString = "&appid=%s&units=imperial";
    private String apiParams = "";
    private static final int MY_PERMISSIONS_REQUEST_CHECK_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationClient;

    String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiString = String.format(Locale.getDefault(),apiString,getString(R.string.open_weather_api));
        getLocationDetails();
    }

    /**
     * Check if HomeButton should be enabled
     */
    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
        else
            finish();
    }

    /**
     * Enable Up button only  if there are entries in the back stack
     */
    public void shouldDisplayHomeUp(){
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>1;
        try
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
        }
        catch(NullPointerException exc)
        {
            Log.e("WeatherActivity",exc.getMessage());
        }
    }

    /**
     * This method is called when the up button is pressed.
     * Pop back stack if so.
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    /**
     * Retrieves today's weather data from Open Weather Map API (JSON)
     */
    public void checkWeather() {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        apiParams = latAndLongString + apiString;
        String connectUrl = weatherUrl + apiParams;
        httpClient.get(connectUrl, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                parseJson(response);
            }

            @Override
            public void onFailure(int errorCode, Header[] headers, Throwable exc, JSONObject response) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),
                        "Problem downloading weather", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });
    }

    /**
     * Parse JSON from server using GSON's implicit parser
     * Initialize and show CurrentWeatherFragment
     * @param jsonResponse - JSONObject representing weather data
     */
    public void parseJson(JSONObject jsonResponse)
    {
        WeatherStatus weatherStatus = new Gson().fromJson(jsonResponse.toString(),WeatherStatus.class);
        launchWeatherFragment(weatherStatus);
    }

    /**
     * Create Parcelable bundle including the apiParams string,
     * Daily Weather Status and City Name.
     * Pass bundle to the CurrentWeatherFragment,
     * add the CurrentWeatherFragment to the Activity's container
     */
    private void launchWeatherFragment(WeatherStatus weatherStatus)
    {
        if(weatherStatus != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("dailyWeather",weatherStatus);
            bundle.putString("apiParams",apiParams);
            bundle.putString("city",city);

            ExtendedForecastFragment extendedForecastFragment = new ExtendedForecastFragment();
            extendedForecastFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,extendedForecastFragment,"extendedWeather")
                    .addToBackStack("extendedWeather")
                    .commit();
        }
        else
        {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),
                "Problem downloading weather", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    /**
     * Check permissions to request location,
     * if user has already granted then proceed.
     * If not, ask for permission and then proceed.
     * Parse longitude, latitude and city from Location if not null.
     */
    private void getLocationDetails() {

        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_CHECK_LOCATION);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double lat = location.getLatitude();
                                double lon = (int) location.getLongitude();

                                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                                List<Address> list = null;
                                try {
                                    list = geoCoder.getFromLocation(location
                                            .getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (list != null & list.size() > 0) {
                                    Address address = list.get(0);
                                    city = address.getLocality();
                                }

                                latAndLongString = String.format(Locale.getDefault(),latAndLongString,lat,lon);
                                checkWeather();
                            }
                        }
                    });
        }
    }

    /**
     * After requesting permissions, determine how to proceed
     * @param requestCode - Code indicating where permissions request originated
     * @param permissions - array of permissions requested
     * @param grantResults - determination of permission granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CHECK_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocationDetails();

                } else {

                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),
                        "You can't access location without this permission", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        }
    }
}