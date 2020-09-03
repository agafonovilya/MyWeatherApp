package ru.geekbrains.myweatherapp.screen.city.searchHistory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.screen.main.MainScreenFragment;

import static android.content.Context.MODE_PRIVATE;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private HistorySource historySource;
    private Activity activity;
    private Fragment fragment;

    public HistoryRecyclerAdapter(HistorySource historySource, Activity activity, Fragment fragment) {
        this.historySource = historySource;
        this.activity = activity;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_history_of_city_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        List<City> listOfCity = historySource.getListOfCity();
        final City city = listOfCity.get(position);
        holder.history_cityName.setText(city.nameOfCity);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latitude = city.lat;
                String longitude = city.lon;
                Bundle result = new Bundle();
                result.putString("lat", latitude);
                result.putString("lon", longitude);
                result.putString("cityName", city.nameOfCity);
                fragment.getParentFragmentManager().setFragmentResult("coord", result);
                ((StartFragment) activity).startFragment(new MainScreenFragment());


                /*saveToSharedPreferences(city);
                activity.onBackPressed();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int) historySource.getCountCity();
    }


    private void saveToSharedPreferences(City city) {
        SharedPreferences sharedPreferences = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentCity", city.nameOfCity);
        editor.putString("latitude", String.valueOf(city.lat));
        editor.putString("longitude", String.valueOf(city.lon));
        editor.commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView history_cityName;
        View cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            history_cityName = cardView.findViewById(R.id.history_cityName);
        }
    }

}
