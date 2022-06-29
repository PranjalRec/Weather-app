package com.pranjal.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView textViewCity,textViewTemp,textViewWeatherCondition,textViewHumidity,textViewMaxTemp,textViewMinTemp,textViewPresure,textViewWind;
    private ImageView image;
    private EditText editTextSearch;
    private Button buttonSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewCity = findViewById(R.id.textViewCityNameWeather);
        textViewTemp = findViewById(R.id.textViewTempWeather);
        textViewWeatherCondition = findViewById(R.id.textViewWeatherConditionWeather);
        textViewHumidity = findViewById(R.id.humidityWeather);
        textViewMaxTemp = findViewById(R.id.maxTempWeather);
        textViewMinTemp = findViewById(R.id.minTempWeather);
        textViewPresure = findViewById(R.id.pressureWeather);
        textViewWind = findViewById(R.id.windWeather);
        image = findViewById(R.id.imageViewWeather);
        editTextSearch = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextSearch.getText().toString();
                getWeatherData(city);

            }
        });
    }

    void getWeatherData(String city){

        WeatherApi weatherApi = RetrofitWeather.getClient().create(WeatherApi.class);
        Call<OpenWeatherMap> call = weatherApi.getWeatherWithCityName(city);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                textViewCity.setText(response.body().getName()+" "+response.body().getSys().getCountry());
                textViewTemp.setText(response.body().getMain().getTemp()+"°C");
                textViewWeatherCondition.setText(response.body().getWeather().get(0).getDescription());
                textViewHumidity.setText(": "+response.body().getMain().getHumidity()+"%");
                textViewMaxTemp.setText(": "+response.body().getMain().getTempMax()+"°C");
                textViewMinTemp.setText(": "+response.body().getMain().getTempMin()+"°C");
                textViewPresure.setText(": "+response.body().getMain().getPressure()+" hPa");
                textViewWind.setText(": "+response.body().getWind().getSpeed());
                String iconCode = response.body().getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/"+iconCode+"@2x.png")
                        .into(image);
            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}