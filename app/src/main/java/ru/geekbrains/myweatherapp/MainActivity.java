package ru.geekbrains.myweatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.geekbrains.myweatherapp.screen.city.searchHistory.App;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.City;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.CityDao;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.HistorySource;
import ru.geekbrains.myweatherapp.screen.main.MainScreenFragment;
import ru.geekbrains.myweatherapp.screen.maps.MapsFragment;

public class MainActivity extends AppCompatActivity implements StartFragment {
    private static final int PERMISSION_REQUEST_CODE = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNotificationChannel();
        firstRunInit();
        startFragment(new MainScreenFragment());
    }

    private void firstRunInit() {
        CityDao cityDao = App.getInstance().getCityHistoryDao();
        HistorySource historySource = new HistorySource(cityDao);

        String latitude;
        String longitude;
        String cityName;
        if(historySource.getCountCity()!= 0) {
            City lastCity = historySource.getListOfCity().get(0);
            latitude = lastCity.lat;
            longitude = lastCity.lon;
            cityName = lastCity.nameOfCity;
        } else {
            latitude = "59.57";
            longitude = "30.190";
            cityName = getResources().getString(R.string.SaintPetersburg);
        }

        Bundle result = new Bundle();
        result.putString("lat", latitude);
        result.putString("lon", longitude);
        result.putString("cityName", cityName);
        getSupportFragmentManager().setFragmentResult("coord", result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.container_for_fragment);
        // Проверяем, что активный фрагмент является фрагментов класса MainScreenFragment
        if (activeFragment!= null && activeFragment.getClass() == MainScreenFragment.class) {
            // Вызываем у активного фрагмента метод onBackPressed
            if (!((MainScreenFragment) activeFragment).onBackPressed()) {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        if (fragment.getClass().equals(MainScreenFragment.class)) {
            getSupportFragmentManager().popBackStackImmediate(MainScreenFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_for_fragment, fragment);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                getSupportFragmentManager().popBackStack();
                startFragment(new MapsFragment());
            }
        }
    }
}
