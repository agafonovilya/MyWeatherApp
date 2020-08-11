package ru.geekbrains.myweatherapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ru.geekbrains.myweatherapp.fragments.FragmentMainScreen;

public class MainActivityByFragment extends AppCompatActivity {
    private static final String TAG = "WEATHER";
    private FragmentMainScreen fragmentMainScreen;

    private WeatherUpdateServiceByBoundService.ServiceBinder serviceBinder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_by_fragment);

        fragmentMainScreen = new FragmentMainScreen();
        startFragment(fragmentMainScreen);
    }

    // Обработка соединения с сервисом
    private ServiceConnection boundServiceConnection = new ServiceConnection() {

        // При соединении с сервисом
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            serviceBinder = (WeatherUpdateServiceByBoundService.ServiceBinder) binder;
        }

        // При разъединении с сервисом
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer !=null && drawer.isDrawerOpen(GravityCompat.START)) {
            fragmentMainScreen.onBackPressed();
        } else {
           super.onBackPressed();
        }
    }

    public void startFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragment != fragmentMainScreen) {
            ft.addToBackStack("");
        }

        ft.replace(R.id.replace_this, fragment);
        ft.commit();
    }

}
