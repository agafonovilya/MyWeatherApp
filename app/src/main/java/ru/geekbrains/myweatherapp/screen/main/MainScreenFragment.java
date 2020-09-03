package ru.geekbrains.myweatherapp.screen.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentResultListener;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.geekbrains.myweatherapp.BuildConfig;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.forecastdata.onecallapi.Daily;
import ru.geekbrains.myweatherapp.forecastdata.onecallapi.Hourly;
import ru.geekbrains.myweatherapp.forecastdata.onecallapi.OneCallRequest;
import ru.geekbrains.myweatherapp.screen.auth.AuthFragment;
import ru.geekbrains.myweatherapp.screen.city.CitySelectionFragment;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.App;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.City;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.CityDao;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.HistorySource;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.HourlyForecastAdapter;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.saved.SavedHourlyForecastAdapter;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.saved.SavedSourceOfHourlyForecastCard;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.SourceOfHourlyForecastCard;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.saved.SavedSourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.saved.SavedWeekForecastAdapter;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.WeekForecastAdapter;
import ru.geekbrains.myweatherapp.weatherRequestInterface.OneCallAPIRequest;

import static android.content.Context.MODE_PRIVATE;

public class MainScreenFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "WEATHER";
    private final String BASE_URL = "https://api.openweathermap.org/";
    private final String EXCLUDE = "curent,hourly,daily";
    private final double ABSOLUTE_ZERO = -273.15;
    private String language = Locale.getDefault().getLanguage();

    private TextView cityName;
    private TextView currentTemperature;
    private TextView lastUpdateTime;
    private TextView currentWeatherDescription;
    private ImageView currentWeatherIcon;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OneCallAPIRequest oneCallAPIRequest;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        loadDataFromSharedPreferences(view);
        initToolbar(view);
        initNavigationView(view);
        initRetrofit();
        setListeners(view);
    }

    private void initView(@NonNull View view) {
        lastUpdateTime = view.findViewById(R.id.mainScreen_lastUpdateTime);
        cityName = view.findViewById(R.id.mainScreen_cityName);
        currentTemperature = view.findViewById(R.id.mainScreen_currentTemperature);
        currentWeatherDescription =  view.findViewById(R.id.mainScreen_currentWeatherDescription);
        currentWeatherIcon = view.findViewById(R.id.mainScreen_currentWeatherIcon);
        swipeRefreshLayout =  view.findViewById(R.id.swipe_refresh);
        toolbar = view.findViewById(R.id.main_fragment_toolbar);
    }

    private void initNavigationView(@NonNull View view) {
        drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.open, R.string.open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = view.findViewById(R.id.mainScreen_navView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar(@NonNull View view) {
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
            case (R.id.mainFragmentToolbar_changeCity):
                ((StartFragment) requireContext()).startFragment(new CitySelectionFragment());
                break;
            case (R.id.mainFragmentToolbar_auth):
                ((StartFragment) requireContext()).startFragment(new AuthFragment());
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            Toast.makeText(requireContext(), R.string.about, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_writeToUs) {
            Toast.makeText(requireContext(), R.string.write_to_us,Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadDataFromSharedPreferences(View view) {
        sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        cityName.setText(sharedPreferences.getString("currentCity", "-"));
        currentTemperature.setText(sharedPreferences.getString("lastTemperature", "-"));
        currentWeatherDescription.setText(sharedPreferences.getString("lastDescription", "-"));

        //Получаем путь к сохраненной иконке погоды. При первом заупуске приложения путь еще не сохранен, поэтому проверяем на null
        String pathToWeatherIcon = sharedPreferences.getString("lastWeatherIcon", null);
        if (pathToWeatherIcon != null){
            loadImageFromStorage(pathToWeatherIcon);
        }

        int sizeOfHourlyForecast = sharedPreferences.getInt("sizeOfHourlyForecast", 48);
        String[] time = new String[sizeOfHourlyForecast];
        String[] temperature = new String[sizeOfHourlyForecast];
        String[] wind = new String[sizeOfHourlyForecast];

        for (int i = 0; i < sizeOfHourlyForecast; i++){
            time[i] = sharedPreferences.getString(("hourlyForecast_time" + i), "-");
            temperature[i] = sharedPreferences.getString(("hourlyForecast_temperature" + i), "-");
            wind[i] = sharedPreferences.getString(("hourlyForecast_wind" + i), "-");
        }

        int sizeOfWeekForecast = sharedPreferences.getInt("sizeOfWeekForecast", 7);
        String[] date = new String[sizeOfWeekForecast];
        String[] dayTemperature = new String[sizeOfWeekForecast];
        String[] nightTemperature = new String[sizeOfWeekForecast];
        for (int i = 0; i < sizeOfWeekForecast; i++) {
            date[i] = sharedPreferences.getString(("weekForecast_date" + i), "-");
            dayTemperature[i] = sharedPreferences.getString(("weekForecast_dayTemperature" + i), "-");
            nightTemperature[i] = sharedPreferences.getString(("weekForecast_nightTemperature" + i), "-");
        }

        updateHourlyForecastRecyclerView(new SavedSourceOfHourlyForecastCard(sizeOfHourlyForecast, time,temperature,wind).build(), view);
        updateWeekForecastRecyclerView(new SavedSourceOfWeekForecastCard(sizeOfWeekForecast, date, dayTemperature, nightTemperature).build(), view);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        oneCallAPIRequest = retrofit.create(OneCallAPIRequest.class);
    }

    private void requestOneCallApi(String latitude, String longitude) {
        oneCallAPIRequest.loadWeather(latitude, longitude, EXCLUDE, BuildConfig.WEATHER_API_KEY, language)
                .enqueue(new Callback<OneCallRequest>() {
                    @Override
                    public void onResponse(Call<OneCallRequest> call, Response<OneCallRequest> response) {
                        Log.d(TAG, "onResponse: " + call.request().toString());
                        if (response.body() != null) {
                            setCurrentWeather(response.body());
                            loadAndSetCurrentWeatherIcon(response.body());
                            initHourlyForecastRecyclerView((new SourceOfHourlyForecastCard(response.body())).build(), requireView());
                            initWeekForecastRecyclerView((new SourceOfWeekForecastCard(response.body())).build(), requireView());
                            swipeRefreshLayout.setRefreshing(false);
                            showSnackbar("Update");
                        }
                    }

                    @Override
                    public void onFailure(Call<OneCallRequest> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + call.request().toString());
                        Log.d(TAG, "onFailure: " + t.toString());
                        swipeRefreshLayout.setRefreshing(false);
                        showSnackbar("Fail update");
                    }
                });
    }

    private void setCurrentWeather(OneCallRequest oneCallRequest) {
        lastUpdateTime.setText(getString(R.string.lastUpdate) + " " + getCurrentDate());
        cityName.setText(sharedPreferences.getString("currentCity", "noCityName"));

        String temperature = String.format("%+.0f", oneCallRequest.getCurrent().getTemp() + ABSOLUTE_ZERO);
        currentTemperature.setText(temperature);
        sharedPreferencesEditor.putString("lastTemperature", temperature);

        String description = StringUtils.capitalize(oneCallRequest.getCurrent().getWeather().get(0).getDescription());
        currentWeatherDescription.setText(description);
        sharedPreferencesEditor.putString("lastDescription", description);

        List<Hourly> hourly = oneCallRequest.getHourly();
        int sizeOfHourlyForecast = hourly.size();
        sharedPreferencesEditor.putInt("sizeOfHourlyForecast", sizeOfHourlyForecast);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < sizeOfHourlyForecast; i++) {
            long unixMilliSeconds = ((long) hourly.get(i).getDt()) * 1000;
            Date time = new Date(unixMilliSeconds);
            String formattedTime = simpleDateFormat.format(time);
            String hourlyTemp = String.format("%+.0f", hourly.get(i).getTemp() + ABSOLUTE_ZERO);
            String hourlyWind = hourly.get(i).getWindSpeed() + getString(R.string.metrsPerSecond);

            sharedPreferencesEditor.putString(("hourlyForecast_time" + i), formattedTime);
            sharedPreferencesEditor.putString(("hourlyForecast_temperature" + i), hourlyTemp);
            sharedPreferencesEditor.putString(("hourlyForecast_wind" + i), hourlyWind);
        }

        List<Daily> daily = oneCallRequest.getDaily();
        int sizeOfWeekForecast = daily.size();
        sharedPreferencesEditor.putInt("sizeOfWeekForecast", sizeOfWeekForecast);
        simpleDateFormat = new SimpleDateFormat("dd MMMM");

        for (int i = 0; i < sizeOfWeekForecast; i++) {
            long unixMilliSeconds = ((long) daily.get(i).getDt()) * 1000;
            Date date = new Date(unixMilliSeconds);
            String formattedDate = simpleDateFormat.format(date);

            String dayTemperature = String.format("%+.0f", daily.get(i).getTemp().getDay() + ABSOLUTE_ZERO);
            String nightTemperature = String.format("%+.0f", daily.get(i).getTemp().getNight() + ABSOLUTE_ZERO);

            sharedPreferencesEditor.putString(("weekForecast_date" + i), formattedDate);
            sharedPreferencesEditor.putString(("weekForecast_dayTemperature" + i), dayTemperature);
            sharedPreferencesEditor.putString(("weekForecast_nightTemperature" + i), nightTemperature);
        }

        sharedPreferencesEditor.apply();
    }

    private void initWeekForecastRecyclerView(SourceOfWeekForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.mainScreen_weekForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        WeekForecastAdapter adapter = new WeekForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    private void updateWeekForecastRecyclerView(SavedSourceOfWeekForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.mainScreen_weekForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        SavedWeekForecastAdapter adapter = new SavedWeekForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    private void initHourlyForecastRecyclerView(SourceOfHourlyForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.mainScreen_hourlyForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        HourlyForecastAdapter adapter = new HourlyForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    private void updateHourlyForecastRecyclerView(SavedSourceOfHourlyForecastCard sourceData, View view){
        RecyclerView recyclerView = view.findViewById(R.id.mainScreen_hourlyForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SavedHourlyForecastAdapter adapter = new SavedHourlyForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);
    }

    private void setListeners(@NonNull final View view) {
        //Устанавливаем слушатель для SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOneCallApi(sharedPreferences.getString("latitude", "59.57"),
                        sharedPreferences.getString("longitude", "30.190"));
            }
        });

        //Устанавливаем слушатель для FragmentResult
        getParentFragmentManager().setFragmentResultListener("coord", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String lat = result.getString("lat");
                String lon = result.getString("lon");
                String cityName = result.getString("cityName");

                sharedPreferencesEditor.putString("currentCity", cityName);
                sharedPreferencesEditor.putString("latitude", lat);
                sharedPreferencesEditor.putString("longitude", lon);
                sharedPreferencesEditor.apply();

                requestOneCallApi(lat, lon);
                addCityToHistory(cityName, lat, lon);
            }
        });

    }

    private void loadAndSetCurrentWeatherIcon(OneCallRequest oneCallRequest) {
       Picasso.get()
               .load("https://openweathermap.org/img/wn/" + oneCallRequest.getCurrent().getWeather().get(0).getIcon() +"@4x.png")
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

    public boolean onBackPressed(){
        if (drawer !=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
       }
    }

    private void addCityToHistory(String name, String lat, String lon) {
        CityDao cityDao = App.getInstance().getCityHistoryDao();
        HistorySource historySource = new HistorySource(cityDao);

        List<City> listOfCity = historySource.getListOfCity();
        for (City city : listOfCity) {
            if (city.nameOfCity.equals(name)) {
                historySource.deleteCity(city);
            }
        }

        City city = new City();
        city.nameOfCity = name;
        city.lat = lat;
        city.lon = lon;
        historySource.addCity(city);
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

}
