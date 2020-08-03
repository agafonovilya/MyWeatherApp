package ru.geekbrains.myweatherapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;

public class WeatherUpdateServiceByBoundService extends Service {
    private final String TAG = "WEATHER";
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Saint Petersburg,RU&appid=";
    private WeatherRequest weatherRequest;

    private final ServiceBinder binder = new ServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private WeatherRequest getWeatherFromServer(){
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    HttpsURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpsURLConnection) uri.openConnection();
                        urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                        urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                        String result = getLines(in);
                        // преобразование данных запроса в модель
                        Gson gson = new Gson();
                        weatherRequest = gson.fromJson(result, WeatherRequest.class);
                    } catch (Exception e) {
                        Log.e(TAG, "Fail connection", e);
                        e.printStackTrace();
                    } finally {
                        Log.d(TAG, "Weather updated");
                        if (null != urlConnection) {
                            urlConnection.disconnect();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (MalformedURLException | InterruptedException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        }
        return weatherRequest;
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    // Класс связи между клиентом и сервисом
    public class ServiceBinder extends Binder {
        public WeatherUpdateServiceByBoundService getService() {
            return WeatherUpdateServiceByBoundService.this;
        }
        public WeatherRequest getWeatherFromServer(){
            return getService().getWeatherFromServer();
        }
    }
}
