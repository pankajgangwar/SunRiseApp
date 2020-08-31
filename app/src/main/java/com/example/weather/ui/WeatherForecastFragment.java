package com.example.weather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.weather.repository.remotedata.FetchWeatherWorker;
import com.example.weather.R;
import com.example.weather.databinding.FragmentMainBinding;
import com.example.weather.repository.db.entity.WeatherEntity;
import com.example.weather.viewmodel.WeatherListViewModel;
import java.util.List;

public class WeatherForecastFragment extends Fragment {

    FragmentMainBinding mBinding;
    WeatherAdapter mWeatherAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        mWeatherAdapter = new WeatherAdapter();
        mBinding.listviewForecast.setAdapter(mWeatherAdapter);
        Button retryButton = mBinding.getRoot().findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySyncAgain();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final WeatherListViewModel viewModel =
                new ViewModelProvider(this).get(WeatherListViewModel.class);
        updateUI(viewModel);
    }

    private void updateUI(WeatherListViewModel viewModel){
        viewModel.getWeatherForecast().observe(getViewLifecycleOwner() , new Observer<List<WeatherEntity>>() {
            @Override
            public void onChanged(List<WeatherEntity> weatherEntities) {
                if(weatherEntities != null && weatherEntities.size() > 0){
                    mBinding.setIsLoading(false);
                    mWeatherAdapter.setWeatherList(weatherEntities);
                }else{
                    mBinding.setIsLoading(true);
                }
            }
        });
    }

    private void trySyncAgain(){
        OneTimeWorkRequest fetchWeatherForecastWorkRequest =
                new OneTimeWorkRequest.Builder(FetchWeatherWorker.class).build();

        WorkManager.getInstance(getActivity()).enqueue(fetchWeatherForecastWorkRequest);
    }
}
