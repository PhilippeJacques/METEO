package com.example.METEO;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.METEO.API.OpenWeatherService;
import com.example.METEO.Model.WeatherResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button;
    private Spinner spinner;
    private Retrofit retrofit;

    private final String apikey="e7635207e7a83be8fa9925c5fc57402a";

    OpenWeatherService service;
    CompositeDisposable compositeDisposable;
    TextView tempText, pressureText,sunrise, sunset,windspeed, windeg, cityText,country;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.sp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        cityText = findViewById(R.id.txt_city);
        tempText = findViewById(R.id.temp);
        pressureText = findViewById(R.id.pressure);
        sunrise = findViewById(R.id.humidity);
        sunset = findViewById(R.id.tempmin);
        windspeed = findViewById(R.id.tempmax);
        imageView = findViewById(R.id.imageView);
        windeg=findViewById(R.id.t_pressure);
        country=findViewById(R.id.city);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String city=spinner.getSelectedItem().toString();
        getWeather(city, apikey);


    }

    private void getWeather(String city, String apikey) {
        compositeDisposable=new CompositeDisposable();

        retrofit= new Retrofit.Builder().
                baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        service = retrofit.create(OpenWeatherService.class);

        compositeDisposable.add(service.getWeather(city,apikey,"metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        System.out.println(weatherResult.getWeathers().get(0).getWeatherMain());
                        switch (weatherResult.getWeathers().get(0).getWeatherMain()) {
                            case "Clear":
                                imageView.setImageResource(R.drawable.clearskyd);
                                break;
                            case "Clouds":
                                if (weatherResult.getWeathers().get(0).getDescription().equals("few clouds: 11-25%")) {
                                    imageView.setImageResource(R.drawable.fewcloudsd);
                                } else if (weatherResult.getWeathers().get(0).getDescription().equals("scattered clouds: 25-50%")) {
                                    imageView.setImageResource(R.drawable.scatteredclouds);
                                } else {
                                    imageView.setImageResource(R.drawable.brokencluds);
                                }
                                break;
                            case "Drizzle":
                                imageView.setImageResource(R.drawable.showerrain);
                                break;
                            case "Rain":
                                if (weatherResult.getWeathers().get(0).getId() >= 500 && weatherResult.getWeathers().get(0).getId() <= 504) {
                                    imageView.setImageResource(R.drawable.raind);
                                } else if (weatherResult.getWeathers().get(0).getId() == 511) {
                                    imageView.setImageResource(R.drawable.snow);
                                } else {
                                    imageView.setImageResource(R.drawable.showerrain);
                                }
                                break;
                            case "Thunderstorm":
                                imageView.setImageResource(R.drawable.thunderstorm);
                                break;
                            case "Snow":
                                imageView.setImageResource(R.drawable.snow);
                                break;
                            default:
                                if (weatherResult.getWeathers().get(0).getId() >= 701 && weatherResult.getWeathers().get(0).getId() <= 781)
                                    imageView.setImageResource(R.drawable.mist);
                                break;
                        }
                        cityText.setText(weatherResult.getName());
                        tempText.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("ºC").toString());
                        pressureText.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append("hPa").toString());
                        sunrise.setText(new StringBuilder(String.valueOf(weatherResult.getSys().getSunrise())).toString());
                        sunset.setText(new StringBuilder(String.valueOf(weatherResult.getSys().getSunset())).toString());
                        windspeed.setText(new StringBuilder(String.valueOf(weatherResult.getWind().getSpeed())).toString());
                        windeg.setText(new StringBuilder(String.valueOf(weatherResult.getWind().getDeg())).toString());
                        country.setText(new StringBuilder(String.valueOf(weatherResult.getSys().getCountry())).toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(),""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }
}