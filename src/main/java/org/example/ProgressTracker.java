package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressTracker {

    private List<String> cities;
    private List<String> prefectures;
    private List<String> stations;
    private List<String> towns;

    private int cityProgress;
    private int prefectureProgress;
    private int stationProgress;
    private int townProgress;

    public ProgressTracker(DataLoader dataLoader) {
        this.cities = dataLoader.getCities();
        this.prefectures = dataLoader.getPrefectures();
        this.stations = dataLoader.getStations();
        this.towns = dataLoader.getTowns();

        this.cityProgress = 0;
        this.prefectureProgress = 0;
        this.stationProgress = 0;
        this.townProgress = 0;
    }

    // Method to check if input matches city, prefecture, or station and update progress
    public boolean checkName(String input, boolean allowCities, boolean allowPrefectures, boolean allowStations, boolean allowTowns) {
        boolean matchFound = false;

        if (allowCities && cities.contains(input)) {
            cityProgress++;
            matchFound = true;
        }
        if (allowPrefectures && prefectures.contains(input)) {
            prefectureProgress++;
            matchFound = true;
        }
        if (allowStations && stations.contains(input)) {
            stationProgress++;
            matchFound = true;
        }
        if (allowTowns && towns.contains(input)) {
            townProgress++;
            matchFound = true;
        }

        return matchFound;
    }

    public void resetProgress() {
        this.cityProgress = 0;
        this.prefectureProgress = 0;
        this.stationProgress = 0;
        this.townProgress = 0;
    }

    // Methods to determine if the input is a city, prefecture, or station
    public boolean isTown(String input) {
        return towns.contains(input);
    }

    public boolean isCity(String input) {
        return cities.contains(input);
    }

    public boolean isPrefecture(String input) {
        return prefectures.contains(input);
    }

    public boolean isStation(String input) {
        return stations.contains(input);
    }

    // Getter methods for progress and total sizes
    public int getTownProgress() {
        return townProgress;
    }

    public int getCityProgress() {
        return cityProgress;
    }

    public int getPrefectureProgress() {
        return prefectureProgress;
    }

    public int getStationProgress() {
        return stationProgress;
    }

    public int getTownTotal() {
        return towns.size();  // Progress + remaining cities
    }

    public int getCityTotal() {
        return cities.size();  // Progress + remaining cities
    }

    public int getPrefectureTotal() {
        return prefectures.size();  // Progress + remaining prefectures
    }

    public int getStationTotal() {
        return stations.size();  // Progress + remaining stations
    }

    // Increment progress methods for loading saved progress
    public void incrementTownProgress() {
        this.townProgress++;
    }

    public void incrementCityProgress() {
        this.cityProgress++;
    }

    public void incrementPrefectureProgress() {
        this.prefectureProgress++;
    }

    public void incrementStationProgress() {
        this.stationProgress++;
    }
}
