package com.pranjal.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather?&appid=c01066c895181a4e9a70f6c6b1514e63&units=metric")
    Call<OpenWeatherMap>getWeatherWithLocation(@Query("lat")double lat,@Query("lon")double lan);

    @GET("weather?&appid=c01066c895181a4e9a70f6c6b1514e63&units=metric")
    Call<OpenWeatherMap>getWeatherWithCityName(@Query("q")String name);

}
