
package com.rowe.adam.switchfourweather.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.name;

/**
 * Model representing WeatherStatus of weather data
 */
public class WeatherStatus implements Serializable, Parcelable
{
    @SerializedName("weather")
    @Expose
    public List<Weather> weather = null;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("list")
    @Expose
    public List<Forecast> forecast = null;


    public final static Creator<WeatherStatus> CREATOR = new Creator<WeatherStatus>() {


        @SuppressWarnings({
            "unchecked"
        })
        public WeatherStatus createFromParcel(Parcel in) {
            WeatherStatus instance = new WeatherStatus();
            in.readList(instance.weather, (Weather.class.getClassLoader()));
            instance.main = ((Main) in.readValue((Main.class.getClassLoader())));
            in.readList(instance.forecast, (Forecast.class.getClassLoader()));
            return instance;
        }

        public WeatherStatus[] newArray(int size) {
            return (new WeatherStatus[size]);
        }

    }
    ;
    private final static long serialVersionUID = -5355414900893548593L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WeatherStatus() {
    }

    /**
     * @param weather
     * @param main
     */
    public WeatherStatus(List<Weather> weather, Main main)
    {
        super();
        this.weather = weather;
        this.main = main;
    }

    /**
     * @param forecast
     */
    public WeatherStatus(List<Forecast> forecast) {
        super();
        this.forecast = forecast;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(weather);
        dest.writeValue(main);
        dest.writeValue(id);
        dest.writeValue(forecast);
    }

    public int describeContents() {
        return  0;
    }

}
