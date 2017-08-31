package com.rowe.adam.switchfourweather.Presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rowe.adam.switchfourweather.Models.Forecast;
import com.rowe.adam.switchfourweather.R;
import com.rowe.adam.switchfourweather.Views.ExtendedForecastFragment;

import java.util.List;
import java.util.Locale;

/**
 * CurrentWeatherFragment
 * Fragment to show today's Weather Outlook
 * @author Adam Rowe
 * @version 1.0
 */
public class ExtendedForecastAdapter extends RecyclerView.Adapter<ExtendedForecastAdapter.ViewHolder> {

    ExtendedForecastFragment.RecyclerViewClickListener listener;
    private Context context;
    private List<Forecast> items;
    private int itemLayout;
    private List<String> days;

    public ExtendedForecastAdapter(ExtendedForecastFragment.RecyclerViewClickListener listener, List<Forecast> items,
                                   List<String> days, int itemLayout) {
        this.listener = listener;
        this.items = items;
        this.itemLayout = itemLayout;
        this.days = days;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    /**
     * When ViewHolder is bound, data is injected to the static View,
     * based on position in RecyclerView
     * @param holder - ViewHolder
     * @param position - location of ViewHolder in RecyclerView
     */
    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        final Forecast item = items.get(position);
        holder.dateStringField.setText(days.get(position));
        String tempString = String.format(Locale.getDefault(),"High: %d°F, Low: %d°F", (int) item.temp.max, (int) item.temp.min);
        holder.tempField.setText(tempString);

        String forecastText = item.weather.get(0).main;
        holder.forecastField.setText(forecastText);

        Glide.with(context)
                .load(String.format("http://openweathermap.org/img/w/%s.png",item.weather.get(0).icon))
                .asBitmap()
                .override(200, 200)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.image.setImageBitmap(resource);
                        holder.itemView.setTag(item);
                    }
                });

        holder.bind(listener);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    /**
     * Static ViewHolder for optimized performance
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tempField;
        TextView forecastField;
        TextView dateStringField;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.weather_item_icon);
            tempField = itemView.findViewById(R.id.temp_field);
            forecastField = itemView.findViewById(R.id.forecast_field);
            dateStringField = itemView.findViewById(R.id.date_string);
        }

        void bind(ExtendedForecastFragment.RecyclerViewClickListener listener) {
            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                listener.onClick(position);
            });
        }
    }

    public void setItems(List<Forecast> items)
    {
        this.items = items;
    }

}
