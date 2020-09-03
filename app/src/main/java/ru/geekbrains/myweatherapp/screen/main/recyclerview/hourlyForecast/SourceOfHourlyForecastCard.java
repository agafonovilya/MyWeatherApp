package ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.geekbrains.myweatherapp.forecastdata.onecallapi.Hourly;
import ru.geekbrains.myweatherapp.forecastdata.onecallapi.OneCallRequest;

public class SourceOfHourlyForecastCard {
    private List<ContentOfHourlyForecastCard> dataSource;
    private List<Hourly> hourly;
    private int size;

    public SourceOfHourlyForecastCard(OneCallRequest oneCallRequest) {
        this.hourly = oneCallRequest.getHourly();
        size = hourly.size();
        dataSource = new ArrayList<>(size);
    }

    public SourceOfHourlyForecastCard build(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        double absoluteZero = -273.15;

        for (int i = 0; i < size; i++) {
            long unixMilliSeconds = ((long) hourly.get(i).getDt()) * 1000;
            Date date = new Date(unixMilliSeconds);
            String formattedTime = simpleDateFormat.format(date);

            String temp = String.format("%+.0f", hourly.get(i).getTemp() + absoluteZero);
            String wind = String.valueOf(hourly.get(i).getWindSpeed());

            dataSource.add(new ContentOfHourlyForecastCard(formattedTime, temp, wind + " m/s"));
        }
        return this;
    }

    public ContentOfHourlyForecastCard getContentOfHourlyForecastCard(int position) {
        return dataSource.get(position);
    }

    public int size(){
        return dataSource.size();
    }

}
