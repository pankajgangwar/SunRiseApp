/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.weather.db;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.example.weather.db.dao.WeatherDao;
import com.example.weather.db.entity.WeatherEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the implementation of {@link com.example.weather.db.dao.WeatherDao}
 */
@RunWith(AndroidJUnit4.class)
public class WeatherDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private WeatherDatabase mDatabase;

    private WeatherDao mWeatherDao;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                WeatherDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();

        mWeatherDao = mDatabase.weather();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void getForecastWhenNoForecastInserted() throws InterruptedException {
        List<WeatherEntity> forecast = LiveDataTestUtil.getValue(mWeatherDao.loadWeatherForecast());
        assertTrue(forecast.isEmpty());
    }

    @Test
    public void getForecastAfterInserted() throws InterruptedException {
        mWeatherDao.insertAll(TestWeatherData.WEATHER_ENTITIES);

        List<WeatherEntity> forecast = LiveDataTestUtil.getValue(mWeatherDao.loadWeatherForecast());

        assertThat(forecast.size(), is(TestWeatherData.WEATHER_ENTITIES.size()));
    }

    @Test
    public void getForecastById() throws InterruptedException {
        mWeatherDao.insertAll(TestWeatherData.WEATHER_ENTITIES);

        WeatherEntity forecast = LiveDataTestUtil.getValue(mWeatherDao.loadForecastById
                (TestWeatherData.WEATHER_ENTITY.getId()));

        assertThat(forecast.getId(), is(TestWeatherData.WEATHER_ENTITY.getId()));
        assertThat(forecast.getDate(), is(TestWeatherData.WEATHER_ENTITY.getDate()));
        assertThat(forecast.getDescription(), is(TestWeatherData.WEATHER_ENTITY.getDescription()));
        assertThat(forecast.getLocation(), is(TestWeatherData.WEATHER_ENTITY.getLocation()));

        assertThat(forecast.getMaxTemp(), is(TestWeatherData.WEATHER_ENTITY.getMaxTemp()));
        assertThat(forecast.getMinTemp(), is(TestWeatherData.WEATHER_ENTITY.getMinTemp()));
    }

}
