package com.example.METEO.API;

import com.example.METEO.Model.WeatherResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherService {
    @GET("weather")
    Observable<WeatherResult> getWeather(@Query("q") String city,
                                         @Query("APPID") String apikey,
                                         @Query("units") String unit);
}
