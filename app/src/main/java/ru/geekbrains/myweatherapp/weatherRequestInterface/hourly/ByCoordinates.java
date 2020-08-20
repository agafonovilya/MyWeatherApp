package ru.geekbrains.myweatherapp.weatherRequestInterface.hourly;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.forecastdata.current.CurrentWeatherRequest;
import ru.geekbrains.myweatherapp.forecastdata.hourly.HourlyForecastRequest;

public interface ByCoordinates {
    @GET("data/2.5/forecast/hourly")
    Call<HourlyForecastRequest> loadWeather(@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String keyApi);
}
