package ru.geekbrains.myweatherapp.screen.citySelectionScreen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.myweatherapp.screen.mapsScreen.MapsFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory.App;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory.City;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory.CityDao;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory.HistoryRecyclerAdapter;
import ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory.HistorySource;

import static android.content.Context.MODE_PRIVATE;

public class CitySelectionFragment extends Fragment {
    private HistoryRecyclerAdapter historyRecyclerAdapter;
    private HistorySource historySource;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_selection, container, false);
        
        setButtonListeners(view);
        initAutoCompeteTextView(container, view);
        initHistory(view);
        return view;
    }

    private void setButtonListeners(View view) {
        //Устанавливаем слушатель для кнопки "назад"
        MaterialButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        
        //Устанавливаем слушатель для кнопки "удалить"
        MaterialButton deleteButton = view.findViewById(R.id.deleteHistory);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historySource.deleteHistory();
                historyRecyclerAdapter.notifyDataSetChanged();
            }
        });

        MaterialButton mapsButton = view.findViewById(R.id.mapButton);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartFragment) requireActivity()).startFragment(new MapsFragment());
            }
        });
    }

    private void initHistory(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.cityHistory_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.requireContext());
        recyclerView.setLayoutManager(layoutManager);

        CityDao cityDao = App.getInstance().getCityHistoryDao();
        historySource = new HistorySource(cityDao);

        historyRecyclerAdapter = new HistoryRecyclerAdapter(historySource, this.requireActivity());
        recyclerView.setAdapter(historyRecyclerAdapter);
    }

    private void initAutoCompeteTextView(final ViewGroup container, View view) {
        // Инициализируем AutoCompleteTextView, передаем массив городов
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.textInputCitySelection);
        autoCompleteTextView.setThreshold(1);   //Минимальное кол-во символов до начала показа подходящих вариантов
        String[] cities = getResources().getStringArray(R.array.arrayOfCity);
        List<String> citiesList = Arrays.asList(cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, citiesList);
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

        // Устанавливаем слушатель на выбор города, с последующим переходом на основной экран
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Проверяем есть ли такой город в списке. Если есть, то удаляем
                List<City> listOfCity = historySource.getListOfCity();
                for (City city : listOfCity) {
                    if (city.nameOfCity.equals(parent.getItemAtPosition(position))) {
                        historySource.deleteCity(city);
                    }
                }

                //Добавляем город в базу данных и обновляем Recycler
                City city = new City();
                addCityToHistory(parent, position, city);
                saveToSharedPreferences(city);
                requireActivity().onBackPressed();
            }
        });
    }

    private void addCityToHistory(AdapterView<?> parent, int position, City city) {
        city.nameOfCity = (String) parent.getItemAtPosition(position);
        city.date = getCurrentDate();
        city.temperature = "-";
        historySource.addCity(city);
        historyRecyclerAdapter.notifyDataSetChanged();
    }

    private void saveToSharedPreferences(City city) {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentCity", city.nameOfCity);
        editor.commit();
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
