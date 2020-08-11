package ru.geekbrains.myweatherapp.screen.settingsScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import ru.geekbrains.myweatherapp.MainActivity;
import ru.geekbrains.myweatherapp.R;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Устанавливаем слушатель для кнопки "назад"
        MaterialButton backButton = (MaterialButton) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).onBackPressed();
            }
        });
        return view;
    }
}
