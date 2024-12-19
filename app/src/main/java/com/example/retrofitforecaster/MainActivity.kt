package com.example.retrofitforecaster

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: WeatherAdapter
    private lateinit var cityNameTextView: TextView

    private var weatherItems: List<WeatherItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.setBackgroundColor(Color.parseColor("#FFCC99"))

        Timber.plant(Timber.DebugTree())
        cityNameTextView = findViewById(R.id.city_name)
        adapter = WeatherAdapter()
        findViewById<RecyclerView>(R.id.r_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // if savedInstanceState is null then make a request else get json from savedInstanceState
        if (savedInstanceState == null) {
            fetchWeather()
        } else {
            val weatherItemsJson = savedInstanceState.getString("weather_data")
            Timber.d("savedInstanceState: Loaded saved weather data from savedInstanceState: ${weatherItemsJson ?: "no data"}")
            if (weatherItemsJson != null) {
                weatherItems = Gson().fromJson(weatherItemsJson, Array<WeatherItem>::class.java).toList()
                adapter.submitList(weatherItems)
                Timber.d("MainActivity: Restored weather data from savedInstanceState")
            } else {
                Timber.w("MainActivity: Saved data is null")
            }
        }
    }

    private fun fetchWeather() {
        val apiKey = "29a24e313af4439bd524fa07c6421932"
        val city = "Shklov"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getWeatherForecast(city, apiKey)

                // log response body to timber
                Timber.d("Response: $response.list")

                weatherItems = response.list.map { weather ->
                    val iconCode = weather.weather[0].icon
                    val temperature = "${(weather.main.temp - 273.15).toInt()}°C"

                    val dateTime = java.util.Date(weather.dt * 1000L)
                    val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                    val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

                    val date = dateFormat.format(dateTime)
                    val time = timeFormat.format(dateTime)

                    val iconUrl = "http://openweathermap.org/img/wn/$iconCode@2x.png"
                    WeatherItem(temperature, iconUrl, time, date)
                }
                adapter.submitList(weatherItems)

                cityNameTextView.text = city

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // override onSaveInstanceState(outState: Bundle) and save response data to bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        weatherItems?.let {
            val weatherItemsJson = Gson().toJson(it)
            outState.putString("weather_data", weatherItemsJson)
            Timber.d("MainActivity: Saved weather data")
        }
    }

}