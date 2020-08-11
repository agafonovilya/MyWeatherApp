package ru.geekbrains.myweatherapp.weatherRequestInterface;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;

public interface CurrentWeatherRequest {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
