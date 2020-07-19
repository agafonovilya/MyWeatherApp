package ru.geekbrains.myweatherapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;

public class FragmentCitySelection extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_selection, container, false);

        //Устанавливаем слушатель для кнопки назад
        MaterialButton backButton = (MaterialButton) view.findViewById(R.id.imageButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityByFragment)getActivity()).onBackPressed();
            }
        });

        // Инициализируем AutoCompleteTextView, передаем массив городов
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.textInputCitySelection);
        autoCompleteTextView.setThreshold(1);   //Минимальное кол-во символов до начала показа подходящих вариантов
        String[] cities = getResources().getStringArray(R.array.arrayOfCity);
        List<String> citiesList = Arrays.asList(cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, citiesList);
        autoCompleteTextView.setAdapter(adapter);

        // Устанавливаем слушатель на прикосновение к строке ввода города, чтобы сразу открывался весь доступный список городов
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.textInputCitySelection);
                autoCompleteTextView.showDropDown();
                return false;
            }
        });

        // Устанавливаем слешатель на выбор города, с последующим переходом на основной экран
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 16.07.2020 обработать выбранный город
                ((MainActivityByFragment)getActivity()).onBackPressed();
                Snackbar.make(container, "Город изменён.", Snackbar.LENGTH_LONG).setAction("Отмена", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 18.07.2020 реализовать отмену выбора города
                    }
                }).show();
            }
        });

        return view;
    }
}
