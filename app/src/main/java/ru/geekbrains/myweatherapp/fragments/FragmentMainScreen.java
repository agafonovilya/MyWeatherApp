package ru.geekbrains.myweatherapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.myweatherapp.BuildConfig;
import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.hourlyForecast.HourlyForecastAdapter;
import ru.geekbrains.myweatherapp.hourlyForecast.SourceOfHourlyForecastCard;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;
import ru.geekbrains.myweatherapp.weekForecast.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.weekForecast.WeekForecastAdapter;

public class FragmentMainScreen extends Fragment implements NavigationView.OnNavigationItemSelectedListener{
    private final String TAG = "WEATHER";
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Saint Petersburg,RU&appid=";

    private TextView currentTemperature;
    private Activity activity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen_with_navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.main_fragment_toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.open, R.string.open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getWeatherFromServer(view);

        //Устанавливаем слушатель для SwipeRefresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherFromServer(view);

                //Устанавливаем задержку анимации
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        //Инициализируем список с карточками прогноза на неделю
        SourceOfWeekForecastCard sourceData = new SourceOfWeekForecastCard(getResources());

        initRecyclerView(sourceData.build(), view);
        initRecyclerViewTwo((new SourceOfHourlyForecastCard(getResources())).build(), view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case (R.id.main_fragment_toolbar_change_city):
                ((MainActivityByFragment)getActivity()).startFragment(3);
                break;
            case (R.id.main_fragment_toolbar_settings):
                ((MainActivityByFragment)getActivity()).startFragment(2);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getWeatherFromServer(final View view) {

        currentTemperature = view.findViewById(R.id.currentTemperature);
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
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
                        Snackbar.make(view, "Fail connection"+BuildConfig.WEATHER_API_KEY, BaseTransientBottomBar.LENGTH_SHORT).show();
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
    }
    private void initRecyclerViewTwo(SourceOfHourlyForecastCard sourceData, View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.hourlyForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        HourlyForecastAdapter adapter = new HourlyForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            Toast.makeText(activity, R.string.about, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_write_to_us) {
            Toast.makeText(activity, R.string.write_to_us,Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
