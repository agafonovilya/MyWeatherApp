package ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast;

public class ContentOfWeekForecastCard {
    private String date;
    private String dayTemperature;
    private String nightTemperature;

    public ContentOfWeekForecastCard(String date, String dayTemperature, String nightTemperature){
        this.date = date;
        this.dayTemperature = dayTemperature;
        this.nightTemperature = nightTemperature;
    }

    public String getDate(){
        return date;
    }

    public String getDayTemperature() {
        return dayTemperature;
    }

    public String getNightTemperature() {
        return nightTemperature;
    }
}
