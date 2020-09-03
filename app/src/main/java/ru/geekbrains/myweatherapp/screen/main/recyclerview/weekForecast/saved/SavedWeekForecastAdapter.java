package ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.saved;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.screen.main.recyclerview.weekForecast.ContentOfWeekForecastCard;

public class SavedWeekForecastAdapter extends RecyclerView.Adapter<SavedWeekForecastAdapter.ViewHolder> {
    private SavedSourceOfWeekForecastCard dataSource;

    public SavedWeekForecastAdapter(SavedSourceOfWeekForecastCard dataSource){
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public SavedWeekForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_week_forecast, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedWeekForecastAdapter.ViewHolder holder, int position) {
        ContentOfWeekForecastCard contentOfWeekForecastCard = dataSource.getContentOfWeekForecastCard(position);
        holder.setData(contentOfWeekForecastCard.getDate(), contentOfWeekForecastCard.getDayTemperature(), contentOfWeekForecastCard.getNightTemperature());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView dayTemperature;
        private TextView nightTemperature;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.weekCard_date);
            dayTemperature = itemView.findViewById(R.id.weekCard_dayTemperature);
            nightTemperature = itemView.findViewById(R.id.weekCard_nightTemperature);
        }

        public void setData(String date, String dayTemperature, String nightTemperature){
            getDate().setText(date);
            getDayTemperature().setText(dayTemperature);
            getNightTemperature().setText(nightTemperature);
        }

        public TextView getDate(){
            return date;
        }

        public TextView getDayTemperature() {
            return dayTemperature;
        }

        public TextView getNightTemperature() {
            return nightTemperature;
        }
    }

}
