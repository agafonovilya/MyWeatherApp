package ru.geekbrains.myweatherapp.screen.city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import ru.geekbrains.myweatherapp.screen.maps.MapsFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.App;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.CityDao;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.HistoryRecyclerAdapter;
import ru.geekbrains.myweatherapp.screen.city.searchHistory.HistorySource;

public class CitySelectionFragment extends Fragment {
    private HistoryRecyclerAdapter historyRecyclerAdapter;
    private HistorySource historySource;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setButtonListeners(view);
        initHistory(view);
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

        //Устанавливаем слушатель для кнопки "карта"
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

        historyRecyclerAdapter = new HistoryRecyclerAdapter(historySource, this.requireActivity(), this);
        recyclerView.setAdapter(historyRecyclerAdapter);
    }

}
