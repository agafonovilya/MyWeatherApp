package ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"name_of_city", "date", "temperature"})})
public class City {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name_of_city")
    public String nameOfCity;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "temperature")
    public String temperature;
}
