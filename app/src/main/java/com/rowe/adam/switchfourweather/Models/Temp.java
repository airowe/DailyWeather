
package com.rowe.adam.switchfourweather.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model representing Temp of weather data
 */
public class Temp implements Serializable, Parcelable
{
    @SerializedName("day")
    @Expose
    public double day;
    @SerializedName("min")
    @Expose
    public double min;
    @SerializedName("max")
    @Expose
    public double max;
    public final static Creator<Temp> CREATOR = new Creator<Temp>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Temp createFromParcel(Parcel in) {
            Temp instance = new Temp();
            instance.day = ((double) in.readValue((double.class.getClassLoader())));
            instance.min = ((double) in.readValue((double.class.getClassLoader())));
            instance.max = ((double) in.readValue((double.class.getClassLoader())));
            return instance;
        }

        public Temp[] newArray(int size) {
            return (new Temp[size]);
        }

    }
    ;
    private final static long serialVersionUID = -9028280345124305119L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Temp() {
    }

    /**
     * @param day
     * @param min
     * @param max
     */
    public Temp(double day, double min, double max) {
        super();
        this.day = day;
        this.min = min;
        this.max = max;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(day);
        dest.writeValue(min);
        dest.writeValue(max);
    }

    public int describeContents() {
        return  0;
    }

}
