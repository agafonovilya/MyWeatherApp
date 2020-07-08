package ru.geekbrains.myweatherapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import ru.geekbrains.myweatherapp.fragments.FragmentCitySelection;
import ru.geekbrains.myweatherapp.fragments.FragmentMainScreen;
import ru.geekbrains.myweatherapp.fragments.FragmentSettings;

public class MainActivityByFragment extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_by_fragment);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentMainScreen fragmentMainScreen = new FragmentMainScreen();
        ft.replace(R.id.replace_this, fragmentMainScreen);
        ft.commit();
    }

    public void startFragment(int item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("");
        if (item == 1) {
            FragmentMainScreen fragmentMainScreen = new FragmentMainScreen();
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
