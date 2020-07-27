package ru.geekbrains.myweatherapp.weekForecast;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.myweatherapp.R;

public class SourceOfWeekForecastCard {
    private List<ContentOfWeekForecastCard> dataSource;
    private Resources resources;    // ресурсы приложения

    public SourceOfWeekForecastCard(Resources resources) {
        dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public SourceOfWeekForecastCard build(){
        String[] date = resources.getStringArray(R.array.date);
        // заполнение источника данных
        for (int i = 0; i < date.length; i++) {
            dataSource.add(new ContentOfWeekForecastCard(date[i]));
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
