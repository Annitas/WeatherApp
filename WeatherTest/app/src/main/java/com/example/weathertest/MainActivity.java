package com.example.weathertest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText citySearch;
    private Button buttonSearch;
    ImageButton reload;
    private TextView weatherResult, humidityResult, feelsResult;
    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        citySearch = findViewById(R.id.citySearch); //search field
        buttonSearch = findViewById(R.id.buttonSearch); //search button
        weatherResult = findViewById(R.id.weatherResult); //result text
        humidityResult = findViewById(R.id.humidityResult); //result humidity
        reload = findViewById(R.id.reload); //reload
        feelsResult = findViewById(R.id.feelsResult);


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(citySearch.getText().toString().trim().equals("")) { //empty string = alert for 3 sec
                    Toast.makeText(MainActivity.this, R.string.noInputAlert, Toast.LENGTH_LONG).show();
                } else {
                    String city = citySearch.getText().toString();
                    String key = "d2b758466054981c7a9596f7549c12be";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
//link for weather data
                    new GetWeatherData().execute(url);
                    saveText();
                    loadText();
                }
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loadText().equals("")) { //empty string = alert for 3 sec
                    Toast.makeText(MainActivity.this, R.string.noInputAlert, Toast.LENGTH_LONG).show();
                } else {
                    String city = loadText();
                    String key = "d2b758466054981c7a9596f7549c12be";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
//link for weather data
                    new GetWeatherData().execute(url);
                    saveText();
                    loadText();
                }
            }
        });//reload


        if (!loadText().isEmpty()) {
            String city = citySearch.getText().toString();
            String key = "d2b758466054981c7a9596f7549c12be";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
            new GetWeatherData().execute(url);
        }
        loadText();
        }

    private void saveText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, citySearch.getText().toString());
        ed.commit();
    }
    private String loadText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        citySearch.setText(savedText);
        return savedText;
    }

    private class GetWeatherData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            weatherResult.setText("Wait..."); //while searching
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader read = null;

            try {
                URL url = new URL(strings[0]); //url connection
                connection = (HttpURLConnection) url.openConnection(); //http connection
                connection.connect();

                InputStream stream = connection.getInputStream(); //get stream
                read = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buff = new StringBuffer();
                String line = "";
                while((line = read.readLine()) != null) {
                    buff.append(line).append("\n");  //line +"\n"
                }
                return buff.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { //close connections
                if (connection != null) {
                    connection.disconnect();
                }
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject object = new JSONObject(result);
                weatherResult.setText(object.getJSONObject("main").getInt("temp") + "°C");
                humidityResult.setText("Humidity: " + object.getJSONObject("main").getInt("humidity"));
                feelsResult.setText("Feels like: " + object.getJSONObject("main").getInt("feels_like") + "°C");
                //set text to field with result"
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    }
