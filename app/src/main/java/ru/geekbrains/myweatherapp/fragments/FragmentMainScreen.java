package ru.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;


import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.geekbrains.myweatherapp.BuildConfig;
import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.hourlyForecast.HourlyForecastAdapter;
import ru.geekbrains.myweatherapp.hourlyForecast.SourceOfHourlyForecastCard;
import ru.geekbrains.myweatherapp.interfaces.CurrentWeather;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;
import ru.geekbrains.myweatherapp.weekForecast.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.weekForecast.WeekForecastAdapter;

public class FragmentMainScreen extends Fragment implements NavigationView.OnNavigationItemSelectedListener{
    private final String TAG = "WEATHER";
    private final String baseURL = "https://api.openweathermap.org/";
    private final double absoluteZero = -273.15;

    private TextView currentTemperature;
    private TextView currentWeatherDescription;
    private ImageView currentWeatherIcon;

    private DrawerLayout drawer;
    private Toolbar toolbar;

    private CurrentWeather currentWeatherInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen_with_navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar(view);
        initNavigationView(view);
        setListeners(view);

        //Инициализируем список с карточками прогноза на неделю
        SourceOfWeekForecastCard sourceData = new SourceOfWeekForecastCard(getResources());
        initWeekForecastRecyclerView(sourceData.build(), view);
        initHourlyForecastRecyclerView((new SourceOfHourlyForecastCard(getResources())).build(), view);

        currentTemperature = view.findViewById(R.id.currentTemperature);
        currentWeatherDescription =  view.findViewById(R.id.currentWeatherDescription);
        currentWeatherIcon = view.findViewById(R.id.currentWeatherIcon);

        initRetrofit();
        requestRetrofit("Saint Petersburg,RU", view);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        currentWeatherInterface = retrofit.create(CurrentWeather.class);
    }

    private void setListeners(@NonNull final View view) {
        //Устанавливаем слушатель для SwipeRefresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                requestRetrofit("Saint Petersburg,RU", view);

                //Устанавливаем задержку анимации обновления страницы
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    private void requestRetrofit(String cityCountry, final View view) {
        currentWeatherInterface.loadWeather(cityCountry, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            loadAndSetCurrentWeatherIcon(response);

                            Double temp = response.body().getMain().getTemp() + absoluteZero;
                            currentTemperature.setText(String.format("%+.0f", temp));
                            currentWeatherDescription.setText(StringUtils.capitalize(response.body().getWeather().get(0).getDescription()));

                            Snackbar.make(view, "Update", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        Snackbar.make(view, "Fail update", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAndSetCurrentWeatherIcon(Response<WeatherRequest> response) {
        Picasso.get()
                .load("https://openweathermap.org/img/wn/" + response.body().getWeather().get(0).getIcon() +"@2x.png")
                .into(currentWeatherIcon);
    }

    private void initNavigationView(@NonNull View view) {
        drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.open, R.string.open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar(@NonNull View view) {
        toolbar = view.findViewById(R.id.main_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
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
                ((MainActivityByFragment)getActivity()).startFragment(new FragmentCitySelection());
                break;
            case (R.id.main_fragment_toolbar_settings):
                ((MainActivityByFragment)getActivity()).startFragment(new FragmentSettings());
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initWeekForecastRecyclerView(SourceOfWeekForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.weekForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        WeekForecastAdapter adapter = new WeekForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }
    private void initHourlyForecastRecyclerView(SourceOfHourlyForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.hourlyForecast);

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
            Toast.makeText(requireContext(), R.string.about, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_write_to_us) {
            Toast.makeText(requireContext(), R.string.write_to_us,Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed(){
        drawer.closeDrawer(GravityCompat.START);
    }

}
