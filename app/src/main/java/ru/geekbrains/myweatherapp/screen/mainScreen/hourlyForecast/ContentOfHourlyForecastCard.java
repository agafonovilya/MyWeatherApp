package ru.geekbrains.myweatherapp.screen.mainScreen.hourlyForecast;

public class ContentOfHourlyForecastCard {
    private String time;
    private String temperature;
    private String windDirection;
    private String wind;

    public ContentOfHourlyForecastCard(String time, String temperature, String windDirection, String wind){
        this.time = time;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.wind = wind;
    }

    public String getTime(){
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWind() {
        return wind;
    }
}
