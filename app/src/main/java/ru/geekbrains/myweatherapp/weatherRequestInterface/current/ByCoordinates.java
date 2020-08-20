package ru.geekbrains.myweatherapp.weatherRequestInterface.current;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.forecastdata.current.CurrentWeatherRequest;

public interface ByCoordinates {
    @GET("data/2.5/weather")
    Call<CurrentWeatherRequest> loadWeather(@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String keyApi);
}
