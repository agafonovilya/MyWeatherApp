package ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast;

public class ContentOfHourlyForecastCard {
    private String time;
    private String temperature;
    private String wind;

    public ContentOfHourlyForecastCard(String time, String temperature, String wind){
        this.time = time;
        this.temperature = temperature;
        this.wind = wind;
    }

    public String getTime(){
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWind() {
        return wind;
    }

}
