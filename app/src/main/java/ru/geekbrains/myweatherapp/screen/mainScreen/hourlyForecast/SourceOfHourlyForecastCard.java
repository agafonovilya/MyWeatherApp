package ru.geekbrains.myweatherapp.screen.mainScreen.hourlyForecast;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.forecastdata.hourly.HourlyForecastRequest;

public class SourceOfHourlyForecastCard {
    private List<ContentOfHourlyForecastCard> dataSource;
    private Resources resources;    // ресурсы приложения

    public SourceOfHourlyForecastCard(Resources resources, HourlyForecastRequest hourlyForecastRequest) {
        dataSource = new ArrayList<>(hourlyForecastRequest.getCnt());
        this.resources = resources;
    }

    public SourceOfHourlyForecastCard build(){
        String[] time = resources.getStringArray(R.array.time);
        // заполнение источника данных
        for (int i = 0; i < dataSource.size(); i++) {
            dataSource.add(new ContentOfHourlyForecastCard(i + ":00", "+22", "S", "5m/s"));
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
