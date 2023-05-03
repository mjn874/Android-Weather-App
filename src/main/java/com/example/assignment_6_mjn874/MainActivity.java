package com.example.assignment_6_mjn874;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    private static String url = "https://api.open-meteo.com/v1/forecast?latitude=";
    private updateWeather weeklyWeather;
    private HashMap<Integer, String> weekdays = new HashMap<>();
    private HashMap<String, Integer> weekDaysNum = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weekdays.put(0, "Sun");
        weekdays.put(1, "Mon");
        weekdays.put(2, "Tue");
        weekdays.put(3, "Wed");
        weekdays.put(4, "Thr");
        weekdays.put(5, "Fri");
        weekdays.put(6, "Sat");

        weekDaysNum.put("Sunday", 0);
        weekDaysNum.put("Monday", 1);
        weekDaysNum.put("Tuesday", 2);
        weekDaysNum.put("Wednesday", 3);
        weekDaysNum.put("Thursday", 4);
        weekDaysNum.put("Friday", 5);
        weekDaysNum.put("Saturday", 6);

        weeklyWeather = new updateWeather();
        volleyRequest();
    }


    private void volleyRequest() {
        String latitude = weeklyWeather.getLatitude();
        String longitude = weeklyWeather.getLongitude();
        url += latitude;
        url += "&longitude=";
        url += longitude;
        url += "&hourly=temperature_2m,relativehumidity_2m,windspeed_10m&daily=weathercode,winddirection_10m_dominant&current_weather=true&temperature_unit=fahrenheit&windspeed_unit=mph&timezone=America%2FChicago";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String date = response.getJSONObject("current_weather").getString("time").split("T")[0];
                            String weekday = calcDate(date);

                            double currTemp = response.getJSONObject("current_weather").getDouble("temperature");
                            double currWind = response.getJSONObject("current_weather").getDouble("windspeed");
                            int currWindDirection = response.getJSONObject("current_weather").getInt("winddirection");
                            weeklyWeather.setCurrWeather(weekday, currTemp, currWind, currWindDirection);
                            JSONArray hourlyHumidity = response.getJSONObject("hourly").getJSONArray("relativehumidity_2m");

                            weeklyWeather.setHumidity(hourlyHumidity);

                            JSONArray hourlyTemp = response.getJSONObject("hourly").getJSONArray("temperature_2m");

                            weeklyWeather.setWeeklyTemperature(hourlyTemp);

                            JSONArray windSpdForecast = response.getJSONObject("hourly").getJSONArray("windspeed_10m");
                            JSONArray windDirForecast = response.getJSONObject("daily").getJSONArray("winddirection_10m_dominant");

                            weeklyWeather.setWindForecasts(windSpdForecast, windDirForecast);
                            weeklyWeather.calculateAverageHumidity();
                            weeklyWeather.calculateAverageTemperature();
                            weeklyWeather.calculateAverageWindSpeedAndDir();

                            updateCurrWeather();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Fetching Data!",  Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonObjectRequest);

    }
    public void updateCurrWeather() {
        TextView currTemp = (TextView) findViewById(R.id.currTempTV);
        currTemp.setText(weeklyWeather.getCurrTemp() + "°F");

        TextView currHumidity = (TextView) findViewById(R.id.currHumidityTV);
        TextView currWindSpeed = (TextView) findViewById(R.id.currWindSpeedTV);
        TextView currWindDirection = (TextView) findViewById(R.id.currDirectionTV);

        currHumidity.setText(weeklyWeather.getCurrHumidity() + "%");
        currWindSpeed.setText(weeklyWeather.getCurrWindSpeed() + "mph");
        currWindDirection.setText(weeklyWeather.getCurrWindDir() + "°");
        
        updateWeekdays();
        updateTemp();
        updateHumidity();


    }


    public void updateWeekdays() {

        int today = weekDaysNum.get(weeklyWeather.getCurrDay());
        TextView day0 = (TextView) findViewById(R.id.day0TV);
        day0.setText(weekdays.get(today));

        int nextDay = (today < 6) ? today + 1: 0;
        TextView day1 = (TextView) findViewById(R.id.day1TV);
        day1.setText(weekdays.get(nextDay));

        nextDay = (nextDay < 6) ? nextDay + 1: 0;
        TextView day2 = (TextView) findViewById(R.id.day2TV);
        day2.setText(weekdays.get(nextDay));

        nextDay = (nextDay < 6) ? nextDay + 1: 0;
        TextView day3 = (TextView) findViewById(R.id.day3TV);
        day3.setText(weekdays.get(nextDay));

        nextDay = (nextDay < 6) ? nextDay + 1: 0;
        TextView day4 = (TextView) findViewById(R.id.day4TV);
        day4.setText(weekdays.get(nextDay));

        nextDay = (nextDay < 6) ? nextDay + 1: 0;
        TextView day5 = (TextView) findViewById(R.id.day5TV);
        day5.setText(weekdays.get(nextDay));

        nextDay = (nextDay < 6) ? nextDay + 1: 0;
        TextView day6 = (TextView) findViewById(R.id.day6TV);
        day6.setText(weekdays.get(nextDay));
    }

    //update current humidity
    public void updateHumidity() {
        ArrayList<Integer> humidityForecast = weeklyWeather.getWeeklyHumidity();
        TextView day0 = (TextView) findViewById(R.id.currHumidityTV);
        day0.setText(String.valueOf(humidityForecast.get(0)) + "%");

    }

    //update weekly average temperatures
    public void updateTemp() {
        ArrayList<Double> tempForecast = weeklyWeather.getWeeklyTemperature();
        TextView day0 = (TextView) findViewById(R.id.currTempTV);
        day0.setText(String.format("%.2f°F",tempForecast.get(0)));

        TextView day1 = (TextView) findViewById(R.id.day1TempTV);
        day1.setText(String.format("%.2f°F",tempForecast.get(1)));

        TextView day2 = (TextView) findViewById(R.id.day2TempTV);
        day2.setText(String.format("%.2f°F",tempForecast.get(2)));

        TextView day3 = (TextView) findViewById(R.id.day3TempTV);
        day3.setText(String.format("%.2f°F",tempForecast.get(3)));

        TextView day4 = (TextView) findViewById(R.id.day4TempTV);
        day4.setText(String.format("%.2f°F",tempForecast.get(4)));

        TextView day5 = (TextView) findViewById(R.id.day5TempTV);
        day5.setText(String.format("%.2f°F",tempForecast.get(5)));

        TextView day6 = (TextView) findViewById(R.id.day6TempTV);
        day6.setText(String.format("%.2f°F",tempForecast.get(6)));
    }


    private String calcDate(String date) { //code was taken from youtube to calculate day of week given a date using some black magic algorithm
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        int result;

        if (month < 3) {
            month += 12;
            year -= 1;
        }
        int k = year % 100;
        int j = year / 100;

        result = ((day + ((month + 1) * 26) / 10) + k + (k / 4) + (j / 4) + (5 * j)) % 7;
        return days[result];
    }
    /*public void getLocation(View view) {
        Button search = (Button) view;
        EditText longitudeEnter = (EditText) findViewById(R.id.longitudeEnter);
        EditText latitudeEnter = (EditText) findViewById(R.id.latitudeEnter);


        String longitude = longitudeEnter.getText().toString();
        String latitude = latitudeEnter.getText().toString();


        weeklyWeather.setCoordinates(longitude, latitude);
        try {
            updateLocation(Double.valueOf(longitude), Double.valueOf(latitude));
            volleyRequest();
        }
        catch (Exception e) {

        }


    }

    public void updateLocation(double longitude, double latitude) {
        Geocoder gcd = new Geocoder(getApplicationContext());
        try {
            List<Address> addressList = gcd.getFromLocation(latitude,longitude,1);
            if(addressList.size() != 0) {
                String city = addressList.get(0).getLocality();
                String state = addressList.get(0).getAdminArea();

                TextView location = (TextView) findViewById(R.id.locationText);
                location.setText(city + ", " + state);
            }
            else {
                throw new Exception();
            }
        }
        catch (Exception e) {

        }


    }*/

}