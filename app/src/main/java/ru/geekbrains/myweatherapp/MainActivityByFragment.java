package ru.geekbrains.myweatherapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import ru.geekbrains.myweatherapp.fragments.FragmentCitySelection;
import ru.geekbrains.myweatherapp.fragments.FragmentMainScreen;
import ru.geekbrains.myweatherapp.fragments.FragmentSettings;

public class MainActivityByFragment extends AppCompatActivity {
    private static final String TAG = "WEATHER";
    FragmentMainScreen fragmentMainScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_by_fragment);

       FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragmentMainScreen = new FragmentMainScreen();
        ft.replace(R.id.replace_this, fragmentMainScreen);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer !=null && drawer.isDrawerOpen(GravityCompat.START)) {
            fragmentMainScreen.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void startFragment(int item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("");
        if (item == 1) {
             fragmentMainScreen = new FragmentMainScreen();
            ft.replace(R.id.replace_this, fragmentMainScreen);
        } else if (item == 2) {
            FragmentSettings fragmentSettings = new FragmentSettings();
            ft.replace(R.id.replace_this, fragmentSettings);
        } else if (item == 3) {
            FragmentCitySelection fragmentCitySelection = new FragmentCitySelection();
            ft.replace(R.id.replace_this, fragmentCitySelection);
        }
        ft.commit();
    }

}
