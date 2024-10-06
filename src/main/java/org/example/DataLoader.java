package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputReader);
        String line;
        while ((line = reader.readLine()) != null) {
            cities.add(line.trim());
        }
        inputReader.close();
    }

    private void loadPrefectures(String filePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputReader);
        String line;
        while ((line = reader.readLine()) != null) {
            prefectures.add(line.trim());
        }
        reader.close();
    }

    private void loadStations(String filePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputReader);
        String line;
        while ((line = reader.readLine()) != null) {
            stations.add(line.trim());
        }
        reader.close();
    }

    private void loadTowns(String filePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputReader);
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
