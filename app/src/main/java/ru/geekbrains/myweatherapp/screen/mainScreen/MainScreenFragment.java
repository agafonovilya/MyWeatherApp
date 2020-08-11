package ru.geekbrains.myweatherapp.screen.mainScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.squareup.picasso.Target;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.geekbrains.myweatherapp.BuildConfig;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.CitySelectionFragment;
import ru.geekbrains.myweatherapp.screen.settingsScreen.SettingsFragment;
import ru.geekbrains.myweatherapp.screen.mainScreen.hourlyForecast.HourlyForecastAdapter;
import ru.geekbrains.myweatherapp.screen.mainScreen.hourlyForecast.SourceOfHourlyForecastCard;
import ru.geekbrains.myweatherapp.weatherRequestInterface.CurrentWeatherRequest;
import ru.geekbrains.myweatherapp.weatherData.WeatherRequest;
import ru.geekbrains.myweatherapp.screen.mainScreen.weekForecast.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.screen.mainScreen.weekForecast.WeekForecastAdapter;

import static android.content.Context.MODE_PRIVATE;

public class MainScreenFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "WEATHER";
    private final String baseURL = "https://api.openweathermap.org/";
    private final double absoluteZero = -273.15;

    private TextView currentTemperature;
    private TextView currentWeatherDescription;
    private ImageView currentWeatherIcon;

    private DrawerLayout drawer;
    private Toolbar toolbar;

    private CurrentWeatherRequest currentWeatherRequest;
    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen_with_navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        loadDataFromSharedPreferences(view);
        initToolbar(view);
        initNavigationView(view);
        setListeners(view);

        //Инициализируем RecyclerView с карточками прогноза на неделю и почасовыми
        SourceOfWeekForecastCard sourceData = new SourceOfWeekForecastCard(getResources());
        initWeekForecastRecyclerView(sourceData.build(), view);
        initHourlyForecastRecyclerView((new SourceOfHourlyForecastCard(getResources())).build(), view);

        initRetrofit();
        requestRetrofit(sharedPreferences.getString("currentCity", "Saint Petersburg,RU"), view);
    }

    private void loadDataFromSharedPreferences(@NonNull View view) {
        sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE);
        ((TextView) view.findViewById(R.id.cityName)).setText(sharedPreferences.getString("currentCity", "Saint Petersburg,RU"));
        currentTemperature.setText(sharedPreferences.getString("lastTemperature", "-"));

        //Получаем путь к сохраненной иконке погоды. При первом заупуске приложения путь еще не сохранен, поэтому проверяем на null
        String pathToWeatherIcon = sharedPreferences.getString("lastWeatherIcon", null);
        if (pathToWeatherIcon != null){
            loadImageFromStorage(pathToWeatherIcon);
        }
    }

    private void initView(@NonNull View view) {
        currentTemperature = view.findViewById(R.id.currentTemperature);
        currentWeatherDescription =  view.findViewById(R.id.currentWeatherDescription);
        currentWeatherIcon = view.findViewById(R.id.currentWeatherIcon);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        currentWeatherRequest = retrofit.create(CurrentWeatherRequest.class);
    }

    private void requestRetrofit(String cityCountry, final View view) {
        currentWeatherRequest.loadWeather(cityCountry, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            loadAndSetCurrentWeatherIcon(response);

                            String temp = String.format("%+.0f", response.body().getMain().getTemp() + absoluteZero);
                            currentTemperature.setText(temp);
                            sharedPreferences.edit().putString("lastTemperature", temp).apply();

                            currentWeatherDescription.setText(StringUtils.capitalize(response.body().getWeather().get(0).getDescription()));

                            //На этом месте иногда кидаются исключения
                            //java.lang.IllegalArgumentException: No suitable parent found from the given view. Please provide a valid view.
                            Snackbar.make(view, "Update", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        Snackbar.make(view, "Fail update", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
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

    private void loadAndSetCurrentWeatherIcon(Response<WeatherRequest> response) {
       Picasso.get()
               .load("https://openweathermap.org/img/wn/" + response.body().getWeather().get(0).getIcon() +"@4x.png")
               .into(new Target() {
                   @Override
                   public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                       currentWeatherIcon.setImageBitmap(bitmap);
                       //Сохраняем картинку в память, путь к картинке сохраняем в SharedPreferences
                       String path = saveToInternalStorage(bitmap);
                       sharedPreferences.edit().putString("lastWeatherIcon", path).apply();
                   }

                   @Override
                   public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                       Log.d(TAG, "onBitmapFailed: Fail");
                   }

                   @Override
                   public void onPrepareLoad(Drawable placeHolderDrawable) {
                   }
               });

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        File directory = requireActivity().getDir("weatherIcon", Context.MODE_PRIVATE);
        File file = new File(directory,"weatherIcon.png");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            // Используем метод сжатия BitMap объекта для записи в OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {
        try {
            File file = new File(path, "weatherIcon.png");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            currentWeatherIcon.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
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
        ((AppCompatActivity) requireContext()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireContext()).getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                ((StartFragment) requireContext()).startFragment(new CitySelectionFragment());
                break;
            case (R.id.main_fragment_toolbar_settings):
                ((StartFragment) requireContext()).startFragment(new SettingsFragment());
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


    public boolean onBackPressed(){
        if (drawer !=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
       }
    }

}
