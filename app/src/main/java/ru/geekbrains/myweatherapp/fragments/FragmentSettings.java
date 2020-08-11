package ru.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;

public class FragmentSettings extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Устанавливаем слушатель для кнопки "назад"
        MaterialButton backButton = (MaterialButton) view.findViewById(R.id.imageButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivityByFragment)getActivity()).onBackPressed();
            }
        });
        return view;
    }
}
