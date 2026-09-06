package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)  // ← Add this
public class Train {
    @JsonProperty("train_id")
    private String trainId;

    @JsonProperty("train_no")
    private String trainNo;

    private List<List<Integer>> seats;

    @JsonProperty("station_times")
    private Map<String, String> stationTimes;

    private List<String> stations;

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public Map<String, String> getStationTimes() {
        return stationTimes;
    }

    public void setStationTimes(Map<String, String> stationTimes) {
        this.stationTimes = stationTimes;
    }

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public List<List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }

    // Fix typo: "Infor" → "Info"
    public String getTrainInfo() {
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }
}