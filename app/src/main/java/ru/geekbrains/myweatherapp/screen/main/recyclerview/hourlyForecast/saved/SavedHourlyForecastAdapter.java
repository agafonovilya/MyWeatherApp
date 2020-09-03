package ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.saved;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.hourlyForecast.ContentOfHourlyForecastCard;

public class SavedHourlyForecastAdapter extends RecyclerView.Adapter<SavedHourlyForecastAdapter.ViewHolder> {
    private SavedSourceOfHourlyForecastCard dataSource;

    public SavedHourlyForecastAdapter(SavedSourceOfHourlyForecastCard dataSource){
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public SavedHourlyForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_hourly_forecast, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHourlyForecastAdapter.ViewHolder holder, int position) {
        ContentOfHourlyForecastCard contentOfHourlyForecastCard = dataSource.getContentOfHourlyForecastCard(position);
        holder.setData(contentOfHourlyForecastCard);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView temperature;
        private TextView wind;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.hourlyCard_time);
            temperature = itemView.findViewById(R.id.hourlyCard_temperature);
            wind = itemView.findViewById(R.id.hourlyCard_wind);
        }

        public void setData(ContentOfHourlyForecastCard contentOfHourlyForecastCard){
            if(contentOfHourlyForecastCard.getTime().equals("00:00")) {
                time.setTextColor(Color.RED);
            } else {
                time.setTextColor(Color.BLACK);
            }
            time.setText(contentOfHourlyForecastCard.getTime());
            temperature.setText(contentOfHourlyForecastCard.getTemperature());
            wind.setText(contentOfHourlyForecastCard.getWind());
        }
    }

}
