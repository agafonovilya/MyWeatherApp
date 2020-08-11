package ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);

    @Update
    void updateCity(City city);

    @Delete
    void deleteCity(City city);

    @Query("DELETE FROM City")
    void deleteAllCity();

    @Query("SELECT * FROM City ORDER BY id DESC")
    List<City> getAllCity();

    @Query("SELECT COUNT() FROM City")
    long getCountCity();

}
