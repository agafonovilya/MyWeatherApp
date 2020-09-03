package ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.saved;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.ContentOfWeekForecastCard;

public class SavedSourceOfWeekForecastCard {
    private List<ContentOfWeekForecastCard> dataSource;
    private int size;
    private String[] date;
    private String[] dayTemperature;
    private String[] nightTemperature;

    public SavedSourceOfWeekForecastCard(int size, String[] date, String[] dayTemperature, String[] nightTemperature) {
       this.size = size;
       this.date = date;
       this.dayTemperature = dayTemperature;
       this.nightTemperature = nightTemperature;
        dataSource = new ArrayList<>(size);
    }

    public SavedSourceOfWeekForecastCard build(){
        for (int i = 0; i < size; i++) {
            dataSource.add(new ContentOfWeekForecastCard(date[i], dayTemperature[i], nightTemperature[i]));
        }
        return this;
    }

    public ContentOfWeekForecastCard getContentOfWeekForecastCard(int position) {
        return dataSource.get(position);
    }

    public int size(){
        return dataSource.size();
    }
}
