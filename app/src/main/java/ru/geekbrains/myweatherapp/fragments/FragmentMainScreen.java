package ru.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;
import ru.geekbrains.myweatherapp.weekForecastByRecyclerView.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.weekForecastByRecyclerView.WeekForecastAdapter;

public class FragmentMainScreen extends Fragment {
    private final String TAG = "WEATHER";
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Saint Petersburg,RU&appid=";
    private final String WEATHER_API_KEY = "cb26104e3d1cc3dfeaefb672099173b1";

    private TextView currentTemperature;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getWeatherFromServer(view);

        //Устанавливаем слушатель для кнопки "настройки"
        MaterialButton startSettingsFragment = (MaterialButton) view.findViewById(R.id.imageButton);
        startSettingsFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityByFragment)getActivity()).startFragment(2);
            }
        });

        //Устанавливаем слушатель для кнопки "выбор города"
        MaterialButton startCitySelectionFragment = (MaterialButton) view.findViewById(R.id.imageButton3);
        startCitySelectionFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityByFragment)getActivity()).startFragment(3);
            }
        });

        //Устанавливаем слушатель для кнопки обновить погоду
        MaterialButton updateWeather = (MaterialButton) view.findViewById(R.id.updateWeather);
        updateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherFromServer(view);
            }
        });

        //Инициализируем список с карточками прогноза на неделю
        SourceOfWeekForecastCard sourceData = new SourceOfWeekForecastCard(getResources());
        initRecyclerView(sourceData.build(), view);
    }

    private void getWeatherFromServer(final View view) {

        currentTemperature = view.findViewById(R.id.currentTemperature);
        try {
            final URL uri = new URL(WEATHER_URL + WEATHER_API_KEY);
            final Handler handler = new Handler(); // Запоминаем основной поток
            new Thread(new Runnable() {
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
                        final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                        // Возвращаемся к основному потоку
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayWeather(weatherRequest);
                            }
                        });
                    } catch (Exception e) {
                        Snackbar.make(view, "Fail connection", BaseTransientBottomBar.LENGTH_SHORT).show();
                        Log.e(TAG, "Fail connection", e);
                        e.printStackTrace();
                    } finally {
                        if (null != urlConnection) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Snackbar.make(view, "Fail update weather. ", BaseTransientBottomBar.LENGTH_SHORT).show();
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        }
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    private void displayWeather(WeatherRequest weatherRequest){
        Double temp = weatherRequest.getMain().getTemp() - 273.15;
        currentTemperature.setText(String.format("%+.0f", temp));
        Snackbar.make(getView(), "Update", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private void initRecyclerView(SourceOfWeekForecastCard sourceData, View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.weekForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        WeekForecastAdapter adapter = new WeekForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(),  layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);
    }
}
