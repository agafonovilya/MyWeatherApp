package ru.geekbrains.myweatherapp.hourlyForecast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.myweatherapp.R;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {
    private SourceOfHourlyForecastCard dataSource;

    public HourlyForecastAdapter(SourceOfHourlyForecastCard dataSource){
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public HourlyForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_hourly_forecast, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastAdapter.ViewHolder holder, int position) {
        // TODO: 22.07.2020 здесь получаем данные с сервера
        ContentOfHourlyForecastCard contentOfHourlyForecastCard = dataSource.getContentOfHourlyForecastCard(position);
        holder.setData(contentOfHourlyForecastCard);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView temperature;
        private TextView windDirection;
        private TextView wind;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.hourlyCardTime);
            temperature = (TextView) itemView.findViewById(R.id.hourlyCardTemperature);
            windDirection = (TextView) itemView.findViewById(R.id.hourlyCardWindDirection);
            wind = (TextView) itemView.findViewById(R.id.hourlyCardWind);
        }

        public void setData(ContentOfHourlyForecastCard contentOfHourlyForecastCard){
            time.setText(contentOfHourlyForecastCard.getTime());
            temperature.setText(contentOfHourlyForecastCard.getTemperature());
            windDirection.setText(contentOfHourlyForecastCard.getWindDirection());
            wind.setText(contentOfHourlyForecastCard.getWind());
        }
    }

}
