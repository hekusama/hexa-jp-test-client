package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private List<String> cities;
    private List<String> prefectures;
    private List<String> stations;
    private List<String> towns;

    public DataLoader(String citiesFile, String prefecturesFile, String stationsFile, String townsFile) {
        cities = new ArrayList<>();
        prefectures = new ArrayList<>();
        stations = new ArrayList<>();
        towns = new ArrayList<>();
        try {
            loadCities(citiesFile);
            loadPrefectures(prefecturesFile);
            loadStations(stationsFile);
            loadTowns(townsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCities(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            cities.add(line.trim());
        }
        reader.close();
    }

    private void loadPrefectures(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            prefectures.add(line.trim());
        }
        reader.close();
    }

    private void loadStations(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            stations.add(line.trim());
        }
        reader.close();
    }

    private void loadTowns(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            towns.add(line.trim());
        }
        reader.close();
    }

    public List<String> getCities() {
        return cities;
    }

    public List<String> getPrefectures() {
        return prefectures;
    }

    public List<String> getStations() {
        return stations;
    }

    public List<String> getTowns() {
        return towns;
    }

    public int getTotalNames() {
        return cities.size() + prefectures.size() + stations.size() + towns.size();
    }
}
