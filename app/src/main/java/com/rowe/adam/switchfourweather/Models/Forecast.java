
package com.rowe.adam.switchfourweather.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Model representing Forecast of weather data
 */
public class Forecast implements Serializable, Parcelable
{
    @SerializedName("temp")
    @Expose
    public Temp temp;
    @SerializedName("weather")
    @Expose
    public List<Weather> weather = null;
    @SerializedName("pressure")
    @Expose
    public double pressure;
    @SerializedName("humidity")
    @Expose
    public int humidity;
    public final static Creator<Forecast> CREATOR = new Creator<Forecast>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Forecast createFromParcel(Parcel in) {
            Forecast instance = new Forecast();
            instance.temp = ((Temp) in.readValue((Temp.class.getClassLoader())));
            instance.pressure = ((double) in.readValue((double.class.getClassLoader())));
            instance.humidity = ((int) in.readValue((int.class.getClassLoader())));
            in.readList(instance.weather, (Weather.class.getClassLoader()));
            return instance;
        }

        public Forecast[] newArray(int size) {
            return (new Forecast[size]);
        }

    }
    ;
    private final static long serialVersionUID = 1119123907787891640L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Forecast() {
    }

    /**
     * @param humidity
     * @param pressure
     * @param weather
     * @param temp
     */
    public Forecast(Temp temp, double pressure, int humidity, List<Weather> weather) {
        super();
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.weather = weather;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(temp);
        dest.writeValue(pressure);
        dest.writeValue(humidity);
        dest.writeList(weather);
    }

    public int describeContents() {
        return  0;
    }

}
