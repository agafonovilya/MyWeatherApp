package ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.geekbrains.myweatherapp.forecastdata.onecallapi.Daily;
import ru.geekbrains.myweatherapp.forecastdata.onecallapi.OneCallRequest;

public class SourceOfWeekForecastCard {
    private List<ContentOfWeekForecastCard> dataSource;
    private List<Daily> daily;
    private int size;

    public SourceOfWeekForecastCard(OneCallRequest oneCallRequest) {
        this.daily = oneCallRequest.getDaily();
        size = daily.size();
        dataSource = new ArrayList<>(size);
    }

    public SourceOfWeekForecastCard build(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        double absoluteZero = -273.15;

        for (int i = 0; i < size; i++) {
            long unixMilliSeconds = ((long) daily.get(i).getDt()) * 1000;
            Date date = new Date(unixMilliSeconds);
            String formattedDate = simpleDateFormat.format(date);

            String dayTemperature = String.format("%+.0f", daily.get(i).getTemp().getDay() + absoluteZero);
            String nightTemperature = String.format("%+.0f", daily.get(i).getTemp().getNight() + absoluteZero);

            dataSource.add(new ContentOfWeekForecastCard(formattedDate, dayTemperature, nightTemperature));
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
