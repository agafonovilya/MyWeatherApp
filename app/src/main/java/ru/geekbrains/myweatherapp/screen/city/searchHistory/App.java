package ru.geekbrains.myweatherapp.screen.city.searchHistory;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    private static App instance;

    private CityDatabase db;
    public  static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        instance = this;

        db = Room.databaseBuilder(
                getApplicationContext(),
                CityDatabase.class,
                "city_history_database")
                .allowMainThreadQueries()   //Только для тестирования
                .build();
    }

    public CityDao getCityHistoryDao(){
        return  db.getCityDao();
    }
}
