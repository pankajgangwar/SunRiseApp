package com.example.weather.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.databinding.ListItemForecastFutureBinding;
import com.example.weather.databinding.ListItemForecastTodayBinding;
import com.example.weather.model.Weather;

import java.util.List;
import java.util.Objects;

public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<? extends Weather> mForecastList;
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private int limit = 7;

    public WeatherAdapter() {
        setHasStableIds(true);
    }

    public void setWeatherList(final List<? extends Weather> weatherList) {
        if (mForecastList == null) {
            mForecastList = weatherList;
            notifyItemRangeInserted(0, weatherList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mForecastList.size();
                }

                @Override
                public int getNewListSize() {
                    return weatherList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mForecastList.get(oldItemPosition).getId() ==
                            weatherList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Weather newWeather = weatherList.get(newItemPosition);
                    Weather oldWeather = mForecastList.get(oldItemPosition);
                    return newWeather.getId() == oldWeather.getId()
                            && Objects.equals(newWeather.getDescription(), oldWeather.getDescription())
                            && Objects.equals(newWeather.getDate(), oldWeather.getDate())
                            && newWeather.getMinTemp() == oldWeather.getMinTemp()
                            && newWeather.getMaxTemp() == oldWeather.getMaxTemp();
                }
            });
            mForecastList = weatherList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_TODAY:
                ListItemForecastTodayBinding todayBinding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_forecast_today,
                                parent, false);

                return new WeatherViewHolderToday(todayBinding);
            case VIEW_TYPE_FUTURE_DAY:
                ListItemForecastFutureBinding futureBinding = DataBindingUtil
                        .inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_forecast_future,
                                parent, false);
                return new WeatherViewHolderFuture(futureBinding);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_TODAY:
                WeatherViewHolderToday holderToday = (WeatherViewHolderToday)holder;
                holderToday.binding.setWeather(mForecastList.get(position));
                holderToday.binding.executePendingBindings();
                break;
            case VIEW_TYPE_FUTURE_DAY:
                WeatherViewHolderFuture holderFuture = (WeatherViewHolderFuture)holder;
                holderFuture.binding.setWeather(mForecastList.get(position));
                holderFuture.binding.executePendingBindings();
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_TODAY;
        }
        return VIEW_TYPE_FUTURE_DAY;
    }


    @Override
    public int getItemCount() {
        if(mForecastList == null) return 0;
        return mForecastList.size();
    }


    @Override
    public long getItemId(int position) {
        return mForecastList.get(position).getId();
    }

    static class WeatherViewHolderToday extends RecyclerView.ViewHolder {

        final ListItemForecastTodayBinding binding;

        public WeatherViewHolderToday(ListItemForecastTodayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class WeatherViewHolderFuture extends RecyclerView.ViewHolder {

        final ListItemForecastFutureBinding binding;

        public WeatherViewHolderFuture(ListItemForecastFutureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
