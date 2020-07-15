package ru.geekbrains.myweatherapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.myweatherapp.MainActivityByFragment;
import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.weekForecastByRecyclerView.SourceOfWeekForecastCard;
import ru.geekbrains.myweatherapp.weekForecastByRecyclerView.WeekForecastAdapter;

public class FragmentMainScreen extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton startSettingsFragment = (ImageButton) view.findViewById(R.id.imageButton);
        startSettingsFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityByFragment)getActivity()).startFragment(2);
            }
        });
        ImageButton startCitySelectionFragment = (ImageButton) view.findViewById(R.id.imageButton3);
        startCitySelectionFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityByFragment)getActivity()).startFragment(3);
            }
        });

        SourceOfWeekForecastCard sourceData = new SourceOfWeekForecastCard(getResources());
        initRecyclerView(sourceData.build(), view);

    }

    private void initRecyclerView(SourceOfWeekForecastCard sourceData, View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.weekForecast);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        WeekForecastAdapter adapter = new WeekForecastAdapter(sourceData);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(),  layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);
    }

}
