package com.example.assignment_6_mjn874;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class updateWeather {
    //hard coded coordinates for now

    private String longitude =  "-95.62"; //default value
    private String latitude =  "29.56"; //default value
    private String currDay;


    private int currHumidity;
    private int currWindDir;
    private double currWindSpeed;
    private double currTemp;

    private JSONArray weeklyHumidity; //7 day hourly humidity
    private JSONArray weeklyTemperature; //7-day hourly temps
    private JSONArray weeklyWindSpeed; // 7 day hourly wind speeds
    private JSONArray weeklyWindDirection; //7 day daily dominant wind dir

    private ArrayList<Integer> avgWeeklyHumidity; //7 day avg humidity
    private ArrayList<Double> avgWeeklyWindSpeed; //7 day avg wind speed
    private ArrayList<Integer> avgWeeklyWindDir; //7 day dominant wind direction
    private ArrayList<Double> avgWeeklyTemp; //7 day avg temp


    public String getCurrTemp() {
        return String.valueOf(currTemp);
    }

    public String getCurrDay() {
        return currDay;
    }

    public String getCurrWindSpeed() {
        return String.valueOf(currWindSpeed);
    }

    public String getCurrWindDir() {
        return String.valueOf(currWindDir);
    }

    public String getCurrHumidity() {
        return String.valueOf(currHumidity);
    }

    public String getLatitude() { return latitude;}

    public String getLongitude() { return longitude;}

    public ArrayList<Double> getWeeklyTemperature() {
        return avgWeeklyTemp;
    }

    public ArrayList<Integer> getWeeklyHumidity() {
        return avgWeeklyHumidity;
    }

    public void setWeeklyTemperature(JSONArray weeklyTemperature) {
        this.weeklyTemperature = weeklyTemperature;
    }

    public void setHumidity(JSONArray humidityForecast) {
        this.weeklyHumidity = humidityForecast;
    }

    public void setWindForecasts(JSONArray windSpdForecast, JSONArray windDirForecast) {
        this.weeklyWindDirection = windDirForecast;
        this.weeklyWindSpeed = windSpdForecast;
    }

    public void setCurrWeather(String day, double temp, double curWindSpeed, int windDir) {
        this.currDay = day;
        this.currTemp = temp;
        this.currWindSpeed = curWindSpeed;
        this.currWindDir = windDir;
    }


    public updateWeather() {
        avgWeeklyHumidity = new ArrayList<>(7);
        avgWeeklyTemp = new ArrayList<>(7);
        avgWeeklyWindSpeed = new ArrayList<>(7);
        avgWeeklyWindDir = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            avgWeeklyHumidity.add(-999);
            avgWeeklyTemp.add(-999.0);
            avgWeeklyWindSpeed.add(-999.0);
            avgWeeklyWindDir.add(-999);
        }

    }

    public void calculateAverageHumidity() throws JSONException {
        int humiditySum = 0;
        int day = 0;
        for (int i = 0; i < weeklyHumidity.length(); i += 24) { //iterate by day
            for (int j = i; j < i + 24; j++) { //iterate hourly
                humiditySum += weeklyHumidity.getInt(j);
            }
            avgWeeklyHumidity.set(day, humiditySum / 24);
            humiditySum = 0;
            day++;
        }
        //get todays humidity
        currHumidity = avgWeeklyHumidity.get(0);
    }

    public void calculateAverageTemperature() throws JSONException {
        double tempSum = 0;
        int day = 0;

        for (int i = 0; i < weeklyTemperature.length(); i += 24) { //iterate by day
            for (int j = i; j < i + 24; j++) { //iterate hourly
                tempSum += weeklyTemperature.getDouble(j);
            }
            avgWeeklyTemp.set(day, tempSum / 24.0);
            tempSum = 0;
            day++;
        }
    }

    public void calculateAverageWindSpeedAndDir() throws JSONException {
        double sum = 0;
        int day = 0;

        for (int i = 0; i < weeklyWindSpeed.length(); i += 24) { //iterate by day
            for (int j = i; j < i + 24; j++) { //iterate hourly
                sum += weeklyWindSpeed.getDouble(j);
            }
            avgWeeklyWindSpeed.set(day, sum / 24.0);
            sum = 0;
            day++;
        }

        for (int i = 0; i < weeklyWindDirection.length(); i++) {
            avgWeeklyWindDir.set(i, weeklyWindDirection.getInt(i));
        }

    }

}
