package com.pranjal.weather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textViewCity,textViewTemp,textViewWeatherCondition,textViewHumidity,textViewMaxTemp,textViewMinTemp,textViewPresure,textViewWind;
    private ImageView image;
    private FloatingActionButton floatingActionButton;
    double lon,lat;

    LocationManager locationManager;
    LocationListener locationListener;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCity = findViewById(R.id.textViewCityName);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewWeatherCondition = findViewById(R.id.textViewWeatherCondition);
        textViewHumidity = findViewById(R.id.humidity);
        textViewMaxTemp = findViewById(R.id.maxTemp);
        textViewMinTemp = findViewById(R.id.minTemp);
        textViewPresure = findViewById(R.id.pressure);
        textViewWind = findViewById(R.id.wind);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        image = findViewById(R.id.imageView);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();

            }
        });

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("lat",String.valueOf(lat));
                Log.e("lon",String.valueOf(lon));

                getWeatherData(lat,lon);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };



        locationListener.onProviderDisabled(new String());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,
                    50,locationListener);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1 && permissions.length>0 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,
                    50,locationListener);
        }
    }

    public void getWeatherData(double lat,double lon){
        WeatherApi weatherApi = RetrofitWeather.getClient().create(WeatherApi.class);
        Call<OpenWeatherMap> call = weatherApi.getWeatherWithLocation(lat,lon);

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
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}