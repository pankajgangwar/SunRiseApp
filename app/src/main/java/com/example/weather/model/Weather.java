package com.example.weather.model;

public interface Weather {
    long getId();
    String getDate();
    String getDescription();
    String getMaxTemp();
    String getMinTemp();
    String getLocation();
}
