package ru.geekbrains.myweatherapp.weekForecast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.geekbrains.myweatherapp.R;

public class WeekForecastAdapter extends RecyclerView.Adapter<WeekForecastAdapter.ViewHolder> {
    private SourceOfWeekForecastCard dataSource;

    public WeekForecastAdapter(SourceOfWeekForecastCard dataSource){
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public WeekForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_week_forecast, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekForecastAdapter.ViewHolder holder, int position) {
        ContentOfWeekForecastCard contentOfWeekForecastCard = dataSource.getContentOfWeekForecastCard(position);
        holder.setData(contentOfWeekForecastCard.getDate());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.textView4);
        }

        public void setData(String date){
            getDate().setText(date);
        }

        public TextView getDate(){
            return date;
        }
    }
}
