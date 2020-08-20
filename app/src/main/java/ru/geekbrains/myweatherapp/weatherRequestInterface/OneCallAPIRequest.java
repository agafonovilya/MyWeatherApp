package ru.geekbrains.myweatherapp.weatherRequestInterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.forecastdata.hourly.HourlyForecastRequest;

public interface OneCallAPIRequest {
    @GET("data/2.5/onecall")
    Call<OneCallAPIRequest> loadWeather(@Query("lat") String latitude, @Query("lon") String longitude,@Query(" exclude") String exclude, @Query("appid") String keyApi);
}
