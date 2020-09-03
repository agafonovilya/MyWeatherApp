package ru.geekbrains.myweatherapp.weatherRequestInterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;

public interface WeatherRequestByCoordinates {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String keyApi);
}
