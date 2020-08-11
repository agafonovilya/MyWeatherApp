package ru.geekbrains.myweatherapp.screen.citySelectionScreen.searchHistory;

import java.util.List;

public class HistorySource {
    private final CityDao cityDao;

    private List<City> listOfCity;

    public HistorySource(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    public List<City> getListOfCity(){
        if (listOfCity == null) {
            loadListOfCity();
        }
        return listOfCity;
    }

    private void loadListOfCity() {
        listOfCity = cityDao.getAllCity();
    }

    public long getCountCity() {
        return cityDao.getCountCity();
    }

    public void addCity(City city) {
        cityDao.insertCity(city);
        loadListOfCity();
    }

    public void updateCity(City city) {
        cityDao.updateCity(city);
        loadListOfCity();
    }

    public void deleteCity(City city) {
        cityDao.deleteCity(city);
        loadListOfCity();
    }

    public void deleteHistory() {
        cityDao.deleteAllCity();
        loadListOfCity();
    }
}
