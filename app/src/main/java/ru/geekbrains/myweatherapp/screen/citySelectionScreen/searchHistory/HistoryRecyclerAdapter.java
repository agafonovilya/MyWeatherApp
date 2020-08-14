package ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.myweatherapp.R;

import static android.content.Context.MODE_PRIVATE;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private HistorySource historySource;
    private Activity activity;

    public HistoryRecyclerAdapter(HistorySource historySource, Activity activity) {
        this.historySource = historySource;
        this.activity = activity;
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
        holder.history_cityRequestDate.setText(city.date);
        holder.history_temperature.setText(city.temperature);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("currentCity", city.nameOfCity);
                editor.commit();
                activity.onBackPressed();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int) historySource.getCountCity();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView history_cityName;
        TextView history_cityRequestDate;
        TextView history_temperature;
        View cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            history_cityName = cardView.findViewById(R.id.history_cityName);
            history_cityRequestDate = cardView.findViewById(R.id.history_cityRequestDate);
            history_temperature = cardView.findViewById(R.id.history_temperature);
        }
    }

}
