package ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.saved;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.ContentOfHourlyForecastCard;

public class SavedSourceOfHourlyForecastCard {
    private final String TAG = "WEATHER";
    private List<ContentOfHourlyForecastCard> dataSource;
    private int size;
    private String[] time;
    private String[] temp;
    private String[] wind;

    public SavedSourceOfHourlyForecastCard(int size, String[] time, String[] temp, String[] wind) {
        this.size = size;
        this.time = time;
        this.temp = temp;
        this.wind = wind;
        dataSource = new ArrayList<>(size);
    }

    public SavedSourceOfHourlyForecastCard build(){
        for (int i = 0; i < size; i++) {
            dataSource.add(new ContentOfHourlyForecastCard(time[i], temp[i], wind[i]));
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
