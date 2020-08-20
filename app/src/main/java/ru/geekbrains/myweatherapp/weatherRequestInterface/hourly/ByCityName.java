package ru.geekbrains.myweatherapp.weatherRequestInterface.hourly;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.forecastdata.current.CurrentWeatherRequest;
import ru.geekbrains.myweatherapp.forecastdata.hourly.HourlyForecastRequest;

public interface ByCityName {
    @GET("data/2.5/forecast/hourly")
    Call<HourlyForecastRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
