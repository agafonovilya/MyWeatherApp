package ru.geekbrains.myweatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ru.geekbrains.myweatherapp.screen.mainScreen.MainScreenFragment;

public class MainActivity extends AppCompatActivity implements StartFragment {
    private static final String TAG = "WEATHER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_by_fragment);

        startFragment(new MainScreenFragment());
    }

    @Override
    public void onBackPressed() {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.container_for_fragment);
        // Проверяем, что активный фрагмент является фрагментов класса MainScreenFragment
        if (activeFragment!= null && activeFragment.getClass() == MainScreenFragment.class) {
            // Вызываем у активного фрагмента метод onBackPressed
            if (!((MainScreenFragment) activeFragment).onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragment.getClass() != MainScreenFragment.class) {
            ft.addToBackStack("");
        }

        ft.replace(R.id.container_for_fragment, fragment);
        ft.commit();
    }
}
