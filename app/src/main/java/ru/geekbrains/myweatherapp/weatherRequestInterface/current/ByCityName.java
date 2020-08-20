package ru.geekbrains.myweatherapp.weatherRequestInterface.current;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.forecastdata.current.CurrentWeatherRequest;

public interface ByCityName {
    @GET("data/2.5/weather")
    Call<CurrentWeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
